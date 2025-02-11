package com.quebecteh.modules.migrators.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.quebecteh.modules.commons.clients.api.zoho.connector.ZohoConnectorProperties;
import com.quebecteh.modules.commons.clients.api.zoho.model.ZohoResponse;
import com.quebecteh.modules.migrators.domain.MigrationLog;
import com.quebecteh.modules.migrators.repository.MigrationLogRepository;
import com.quebecteh.modules.migrators.service.JDBCSourceConnectionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class CustomerUPDATEController {

	private static final String RESOURCE = "contacts";
	private static final String DESTINATION_ENTITY = "contact";
	
	final ZohoConnectorProperties connProperties;
	final MigrationLogRepository migrationLogRepository;
	final JDBCSourceConnectionService connectionService;
	@Value("${migrations.zoho.inventory.organization_id}")
	protected String zohoOrganizationId;
	@Value("${migrations.zoho.inventory.authToken}")
	String zohoAuthToken;
	
	LocalDateTime authTokenDataCreated = LocalDateTime.now().minusMinutes(90);
	String authToken;
	
	final static String DEFAULT_ROUTE_CUSTOM_FIELD = "5304025000018147823";
	final static String SALES_REP_CUSTOM_FIELD = "5304025000000168237";
	
	private Map<Object,Object> salesRepMap = Map.of(
			26, "5304025000004163152", //Alexa Paris
			25, "5304025000001849001", //Teka Lisboa
			28, "5304025000017444581", //Pedro Forte
			24, "5304025000000157015", //Ronaldo Giusti
			22, "5304025000001837001", //Sergio Vechiatto
			37, "5304025000017448538", //Ofelia Urquizo
			40, "5304025000017545445", //Leticia Mendes
			41, "5304025000017444573" //Gabriela Mendonca
		);
	
	public void updateAreaAndSalesResp() throws SQLException {

		
		var sql = "select ml.id as id_log, ml.destination_id, c.id as id_customer, c.business_name as customer_name,\r\n"
				+ "		at.area_name, sp.id as id_sales_rep, concat(sp.first_name, ' ',sp.last_name) as sales_rep\r\n"
				+ "from customer c \r\n"
				+ "join area_territory at on at.id = c.id_area_territory \r\n"
				+ "join system_persona sp on  sp.id = c.id_system_persona_sales_rep\r\n"
				+ "join migration_log ml on ml.source_id = c.id+''\r\n"
				+ "and ml.source_entity = 'customer'\r\n"
				+ "and ml.status = 'success'\r\n"
				+ "order by c.id\r\n";
		
		var resultSet =  connectionService.queryToMap(sql);
		
		resultSet.forEach( customerMap -> {
			
			String salesRepId = (String) salesRepMap.get(customerMap.get("id_sales_rep"));
			
			Map<String, Object> bodyMap = Map.of(
					"custom_fields", List.of(
							Map.of(
									"customfield_id", DEFAULT_ROUTE_CUSTOM_FIELD,
									"value", customerMap.get("area_name") 
							),
							Map.of(
									"customfield_id", SALES_REP_CUSTOM_FIELD,
									"value", salesRepId
							)
					)
			);
			
			
			
			ObjectMapper objectMapper = new ObjectMapper();
			String json = ""; 
	        try {
				json = objectMapper.writeValueAsString(bodyMap);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        System.out.println("------------------------------");
	        System.out.println(customerMap.get("customer_name"));
	        System.out.println(json);
	        System.out.println("------------------------------");
	        
	        var migrationLog = migrationLogRepository.findById(Long.valueOf(customerMap.get("id_log").toString())).get();
			update(RESOURCE, migrationLog, json);
		});
	}
	
	@SuppressWarnings("unchecked")
	public void update(String resourceName, MigrationLog log, String body) {
        // Monta a URL de alteraçao
        String url = String.format(
            "%s/%s/%s?organization_id=%s",
            connProperties.getApiBaseUrlInventory(),
            resourceName,
            log.getDestinationId(),
            this.zohoOrganizationId
        );
        
   
        HttpClient client = HttpClient.newHttpClient();


        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Zoho-oauthtoken " + getAuthKey())
            .PUT(HttpRequest.BodyPublishers.ofString(body))
            .build();

        try {
            // Envia a requisição e captura a resposta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            // Status e corpo da resposta
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

            var bodyMap =  getObjectMapper(response.body(), DESTINATION_ENTITY);
            // Verifica se a exclusão foi bem-sucedida
            if (response.statusCode() == 200) {
                System.out.println("alterado com sucesso.");
    			log.getDestinationData().setData( (Map<String, Object>) bodyMap.getBody());
                log.setStatus(MigrationLog.UPDATED);
            } else {
                System.out.println("Falha ao ALTERAR. Verifique a mensagem de retorno acima: " + log.getDestinationId());
                log.setStatus(MigrationLog.UPDATE_ERROR);
            }
            log.setMessage("code " + response.statusCode() + " " + bodyMap.getMessage());
            migrationLogRepository.save(log);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
	
	protected ZohoResponse getObjectMapper(String responseBody, String node) throws JsonProcessingException, JsonMappingException {
	    ObjectMapper objectMapper = JsonMapper
	        .builder()
	        .addModule(new JavaTimeModule())
	        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
	        .build();
	    
	    String body = responseBody.replace("\""+node+"\"", "\"body\"");
	
	    ZohoResponse apiResponse = objectMapper.readValue(body, ZohoResponse.class);
	    return apiResponse;
	}
	
	
	protected String getAuthKey() {
		
		String refreshtokenUrl = "https://accounts.zoho.com/oauth/v2/token?refresh_token=1000.4a5e3618d98d7539901ba500b7ca1549.bf057522f686f02fa02654ac2a485d2b&client_id=1000.786MVQWEOFKTV2T9X1UYKD8UAOANXO&client_secret=6af1aa4800240393e2ec05213b0a1d741d0f52ad66&redirect_uri=http://zrkgqo.conteige.cloud/bmodule-picklist/zoho/auth/callback&grant_type=refresh_token&state=bave";
		long minutesPassed = Duration.between(authTokenDataCreated, LocalDateTime.now()).toMinutes();
		if (minutesPassed > 55) {
			log.info("Refresh Token");
			authToken = post(refreshtokenUrl);
			authTokenDataCreated = LocalDateTime.now();
		}
		
		return authToken;
		
	}
	
	
	protected String post(String url) {
		
		
		log.info("Executing POST {}", url);
	
		
		HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(url))
	            .header("Content-Type", "application/json")
	            .POST(HttpRequest.BodyPublishers.noBody())
	            .build();
		
		HttpClient client = HttpClient.newHttpClient();
		
		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());	
			ObjectMapper objectMapper = new ObjectMapper();
	        @SuppressWarnings("unchecked")
			Map<String, Object> map = objectMapper.readValue(response.body(), Map.class);
			return map.get("access_token").toString();
		
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
		
	}
    
}

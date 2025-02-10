package com.quebecteh.modules.migrators.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
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
public class InvoiceUPDATEController {

	private static final String RESOURCE = "invoices";
	private static final String DESTINATION_ENTITY = "invoice";
	
	final ZohoConnectorProperties connProperties;
	final MigrationLogRepository migrationLogRepository;
	final JDBCSourceConnectionService connectionService;
	@Value("${migrations.zoho.inventory.organization_id}")
	protected String zohoOrganizationId;
	@Value("${migrations.zoho.inventory.authToken}")
	String zohoAuthToken;
	
	LocalDateTime authTokenDataCreated = LocalDateTime.now().minusMinutes(90);
	String authToken;
	
	
	public void updateAllforDiffFix() throws SQLException {

		
		var sql = "select 	i.id,\r\n"
				+ "			ml.id as log_id, \r\n"
				+ "			mdd.jdata ->> '$.invoice_number' as zoho_number, \r\n"
				+ "			mdd.jdata ->> '$.invoice_id' as zoho_invoice_id,\r\n"
				+ "			i.value_total_invoice - (mdd.jdata ->> '$.total') * 1.0 as diff,   \r\n"
				+ "			i.notes_internal \r\n"
				+ "	from  migration_log ml \r\n"
				+ "	inner join invoice i on i.id+'' = ml.source_id \r\n"
				+ "	inner join migration_destination_data mdd on mdd.migration_log_id = ml.id\r\n"
				+ "	where ml.destination_entity = 'invoice'\r\n"
				+ "	and ml.status = 'success'\r\n"
				+ "	and  (mdd.jdata ->> '$.total') * 1.0 <> i.value_total_invoice\r\n"
				+ "	order by i.id desc";
		
		var resultSet =  connectionService.queryToMap(sql);
		
		resultSet.forEach( invoiceMap -> {
			
			Map<String, Object> bodyMap = Map.of(
					"adjustment_description", "Divergence adjustment",
					"adjustment", invoiceMap.get("diff"),
					"notes", "- Adjustment of "+invoiceMap.get("diff")+" made according to the value of the invoice in CONCIAT",
					"reason", "Adjustment of "+invoiceMap.get("diff")+" made according to the value of the invoice in CONCIAT"
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
	        System.out.println(invoiceMap.get("zoho_number"));
	        System.out.println(json);
	        System.out.println("------------------------------");
	        
	        var migrationLog = migrationLogRepository.findById(Long.valueOf(invoiceMap.get("log_id").toString())).get();
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
                System.out.println("Falha ao excluir. Verifique a mensagem de retorno acima: " + log.getDestinationId());
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

package com.quebecteh.modules.migrators.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
public class SalesOrdersUPDATEController {

	private static final String RESOURCE = "salesorders";
	private static final String DESTINATION_ENTITY = "salesorder";
	
	private static final String COALHO_CHEESE_PRODUCT_ID = "1052"; 
	private static final String COALHO_CHEESE_ITEM_ID = "5304025000002402224";
	
	final ZohoConnectorProperties connProperties;
	final MigrationLogRepository migrationLogRepository;
	final JDBCSourceConnectionService connectionService;
	@Value("${migrations.zoho.inventory.organization_id}")
	protected String zohoOrganizationId;
	@Value("${migrations.zoho.inventory.authToken}")
	String zohoAuthToken;
	
	LocalDateTime authTokenDataCreated = LocalDateTime.now().minusMinutes(90);
	String authToken;
	
	
	public void updateAllforCoalhoCheese() throws SQLException {
		
		
		
		var sql = "select i.id, so.id as id_sales_order, concat('CINV-',i.invoice_number), sum(d.total_weight) as weight\r\n"
				+ "from invoice i \r\n"
				+ "join invoice_detail d on d.id_invoice = i.id\r\n"
				+ "join products p on p.id = d.id_product\r\n"
				+ "join sales_order so on so.id = i.id_sales_order\r\n"
				+ "where d.value_unit <=10\r\n"
				+ "and p.id = "+COALHO_CHEESE_PRODUCT_ID+"\r\n"
				+ "and truncate(d.total_weight * d.value_unit,0) = truncate(d.value_total,0)\r\n"
				+ "and i.value_total_invoice > 0\r\n"
				+ "and i.id_invoice_status <> 4\r\n"
				+ "and so.id_sales_order_status <> 5\r\n"
				+ "group by i.id, so.id, i.invoice_number\r\n"
				+ "order by i.id";
		
		var resultSet =  connectionService.queryToMap(sql);
		
		var sourceIds = resultSet.stream()
		            .map(m -> m.get("id_sales_order"))
		            .map(Object::toString)       
		            .collect(Collectors.toList());
		
		
		var newQuantityMap = resultSet.stream()
	            .collect(Collectors.toMap(
	                    m -> m.get("id_sales_order").toString(),             // Chave do map final
	                    m -> m.get("weight")     // Valor do map final
	              ));
		
		
		var logs = migrationLogRepository.findAllByFiltersAndSourceIds("bave-" + zohoOrganizationId, "zoho-inventory", DESTINATION_ENTITY, "success", sourceIds);
		
		
		logs.forEach( migrationLog -> {
			
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> line_items = (List<Map<String, Object>>) migrationLog.getDestinationData().getData().get("line_items");
			var lineItemsCoalhoCheese = line_items.stream().filter(item -> item.get("item_id").equals(COALHO_CHEESE_ITEM_ID)).collect(Collectors.toList());
			
			System.out.println(lineItemsCoalhoCheese);
			
			var lineItemsUpdate = new ArrayList<Map<String,Object>>();
			lineItemsCoalhoCheese.forEach(lineItem -> {
				lineItemsUpdate.add(
					Map.of(
						"line_item_id", lineItem.get("line_item_id"),
						"unit", "lb",
						"quantity", newQuantityMap.get(migrationLog.getSourceId())
					)
				);
			});
			
			Map<String, Object> bodyMap = Map.of(
					"line_items", lineItemsUpdate
			);
			
			ObjectMapper objectMapper = new ObjectMapper();
			String json = ""; 
	        try {
				json = objectMapper.writeValueAsString(bodyMap);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        System.out.println(json);

			
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

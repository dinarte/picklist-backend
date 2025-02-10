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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quebecteh.modules.commons.clients.api.zoho.connector.ZohoConnectorProperties;
import com.quebecteh.modules.migrators.domain.MigrationLog;
import com.quebecteh.modules.migrators.repository.MigrationLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class CustomerPaymentsDELETEController {

	private static final String RESOURCE = "customerpayments";
	final ZohoConnectorProperties connProperties;
	final MigrationLogRepository migrationLogRepository;
	@Value("${migrations.zoho.inventory.organization_id}")
	protected String zohoOrganizationId;
	@Value("${migrations.zoho.inventory.authToken}")
	String zohoAuthToken;
	
	LocalDateTime authTokenDataCreated = LocalDateTime.now().minusMinutes(90);
	String authToken;
	
	
	public void deleteAll() throws SQLException {
		var logs = migrationLogRepository.findAllByFilters("bave-" + zohoOrganizationId, "zoho-inventory", "payment", "success");
		logs.forEach( log -> {
			delete(RESOURCE, log);
		});
	}
	
	
	
	public void delete(String resourceName, MigrationLog log) {
        // Monta a URL de exclusão
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
            .DELETE()
            .build();

        try {
            // Envia a requisição e captura a resposta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            // Status e corpo da resposta
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

            // Verifica se a exclusão foi bem-sucedida
            if (response.statusCode() == 200) {
                System.out.println("Pagamento excluído com sucesso.");
                log.setStatus(MigrationLog.DELETED);
            } else {
                System.out.println("Falha ao excluir pagamento. Verifique a mensagem de retorno acima.");
                log.setStatus(MigrationLog.DELETE_ERROR);
            }
            log.setMessage("code " + response.statusCode() + " " + response.body());
            migrationLogRepository.save(log);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
	
	protected String getAuthKey() {
		
		String refreshtokenUrl = "https://accounts.zoho.com/oauth/v2/token?refresh_token=1000.30dd6626905c4f21d7479e7d7e554366.2a582e19c688ca36b3a228fe73228b69&client_id=1000.786MVQWEOFKTV2T9X1UYKD8UAOANXO&client_secret=6af1aa4800240393e2ec05213b0a1d741d0f52ad66&redirect_uri=http://zrkgqo.conteige.cloud/bmodule-picklist/zoho/auth/callback&grant_type=refresh_token&state=bave";
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

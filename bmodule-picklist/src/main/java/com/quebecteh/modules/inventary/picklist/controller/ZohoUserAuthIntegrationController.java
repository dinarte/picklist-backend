package com.quebecteh.modules.inventary.picklist.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.quebecteh.modules.commons.connector.model.domain.ZohoConnection;
import com.quebecteh.modules.commons.connector.service.ZohoConnectionService;
import com.quebecteh.modules.inventary.picklist.model.domain.PickListUserAuth;

@CrossOrigin("*")
@RestController
public class ZohoUserAuthIntegrationController {
	
	
	@Autowired
	private JwtHelper jwtHelper;
	
	@Autowired
	PickListUserAuth auth;
	
	@Autowired
	ZohoConnectionService connectionService;
	
	@GetMapping("/{tenantId}/zoho/auth")
	public PickListUserAuth  auth(ZohoConnection conn) {
		
		connectionService.renewConnectionIfExipered(conn);

		//UUID id = UUID.randomUUID();
		
        HttpClient client = HttpClient.newHttpClient();
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://www.zohoapis.com/inventory/v1/settings/signals/sg_bave_picklist_user_l/execute?auth_type=oauth&organization_id=857946007"))
            .header("Content-Type", "application/json")
            .header("Authorization", "Zoho-oauthtoken " + conn.getAccesToken())
            .POST(BodyPublishers.noBody())
            .build();
        
        HttpResponse<String> response;
        try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			 ObjectMapper objectMapper = JsonMapper
			            .builder()
			            .addModule(new JavaTimeModule())
			            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
			            .build();
			        
			        
			 JsonNode jsonNode = objectMapper.readTree(response.body());
			
			String authJSON =  jsonNode.get("response").get("signal_response").toString();	
			auth = objectMapper.readValue(authJSON ,PickListUserAuth.class);
			String jwtToken = jwtHelper.getEncodedJwt(auth);
			//auth.setJwtToken(jwtToken);
			
			jwtHelper.decodeJwt(jwtToken);
			
			return auth;
			        
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e.getMessage());
		}
          
		
	}
	
	

}

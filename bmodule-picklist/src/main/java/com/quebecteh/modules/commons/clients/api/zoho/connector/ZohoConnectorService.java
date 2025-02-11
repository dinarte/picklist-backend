package com.quebecteh.modules.commons.clients.api.zoho.connector;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.quebecteh.modules.commons.clients.api.zoho.model.ZohoApiResponseDTO;
import com.quebecteh.modules.commons.connector.model.dto.ApiEndPointDTO;
import com.quebecteh.modules.commons.connector.model.dto.OrganizationDTO;
import com.quebecteh.modules.inventary.picklist.model.domain.PickListUserAuth;
import com.quebecteh.modules.inventary.picklist.validators.HttpBadRequestException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;


@Service
public class ZohoConnectorService {
	
	@Autowired
    private ZohoConnectorProperties connProperties;
    
	@Autowired
    private HttpServletRequest request;

	@SneakyThrows
	public Map<String, Object> sendPostAuthentication(String tenantId, String code) {
		String callbackUrl = connProperties.getCallbackUrl();
        String tokenUrl = connProperties.getTokenUrl(code, callbackUrl, tenantId);
   
        
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest post = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(post, HttpResponse.BodyHandlers.ofString());
        
        
        if (response.body().contains("error") ) {
        	var bodyMap = new ObjectMapper().readValue(response.body(), Map.class);
            bodyMap = Map.of(	
            					"zohoResponse", bodyMap,
    				    		"tokenUrl", tokenUrl
    				    	);
        	throw new HttpBadRequestException(bodyMap, "zoho-oauth-invalide-post-tokenUrl"); 
        }


        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> authVeluesMap = mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
		return authVeluesMap;
	}
	
	
	@SneakyThrows
	public Map<String, Object> sendPostRenewAuth(String tenentId, String refreshToken) {
		String callbackUrl = connProperties.getCallbackUrl();
        String tokenUrl = connProperties.getRefreshTokenURL(refreshToken, callbackUrl); 
       
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest post = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(post, HttpResponse.BodyHandlers.ofString());


        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> authVeluesMap = mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
		return authVeluesMap;
	}
	
	
	@SneakyThrows
	public Map<String, Object> sendPostRevokeRefreshToken(String refreshToken) {
		String callbackUrl = connProperties.getRevokeRefreshTokenURL(refreshToken);
        String tokenUrl = connProperties.getRefreshTokenURL(refreshToken, callbackUrl); 
       
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest post = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(post, HttpResponse.BodyHandlers.ofString());


        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseMap = mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
		return responseMap;
	}
	
	

    
    @SneakyThrows
    public List<OrganizationDTO> getOrganizations(String accessToken) {
        
        String apiUrl = connProperties.getApiUrl() + "/organizations"; 
    
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest get = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Zoho-oauthtoken " + accessToken)
                .GET() 
                .build();

        HttpResponse<String> response = client.send(get, HttpResponse.BodyHandlers.ofString());
        
        ObjectMapper objectMapper = new ObjectMapper();    
        ZohoApiResponseDTO apiResponse = objectMapper.readValue(response.body(), ZohoApiResponseDTO.class);
        List<OrganizationDTO> organizations = apiResponse.getOrganizations();
        
       
        
        organizations.forEach(organization -> {
        	
        	String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/";
            String endpointUrl = baseUrl + "zoho/organizaton/" + organization.getOrganizationId();
            
            ApiEndPointDTO[] endPoints = {
    			new ApiEndPointDTO("Add organization to the connection", "add-organization-connection", endpointUrl, "GET"),
    		};
            
        	organization.setEndPoints(endPoints);
        });
            
            
        return organizations;
    }
    
    @SneakyThrows
    public PickListUserAuth getUserInfo(String accessToken, String organizationId) {
    	
    	 JsonNode json = retriveUserInfo(accessToken, organizationId);
    	 
    	 System.out.println("TESTE:");
    	 System.out.println(json);
    	 System.out.println("-------------------------------");
    	 
    	 
         if (json == null)
        	 return null;
         

         JsonNode emailId = json.get("email_ids").get(0);
         
         return PickListUserAuth.builder()
		         	.id(json.get("user_id").asText())
		         	.userName(json.get("name").asText())
		         	.userOriginId(json.get("user_id").asText())
		         	.userEmail(emailId.get("email").asText())
		         	.build();
         
    }



	private JsonNode retriveUserInfo(String accessToken, String organizationId)
			throws IOException, InterruptedException, JsonProcessingException, JsonMappingException {
		String orgIdParam = organizationId != null ? "?organization_id="+organizationId : "";
		String apiUrl = "https://www.zohoapis.com/inventory/v1/users/me" + orgIdParam; 
    	    
         HttpClient client = HttpClient.newHttpClient();
         HttpRequest get = HttpRequest.newBuilder()
                 .uri(URI.create(apiUrl))
                 .header("Content-Type", "application/json")
                 .header("Authorization", "Zoho-oauthtoken " + accessToken)
                 .GET() 
                 .build();

         HttpResponse<String> response = client.send(get, HttpResponse.BodyHandlers.ofString());
         
    	 System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
         System.out.println(response.body());
         
         JsonNode json = JsonMapper.builder().build().readTree(response.body()).get("user");
         
		return json;
	}
	
	
}

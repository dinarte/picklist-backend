package com.quebecteh.modules.commons.clients.api.zoho.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.quebecteh.modules.commons.clients.api.zoho.connector.ZohoConnectorProperties;
import com.quebecteh.modules.commons.clients.api.zoho.model.ZohoResponse;

@Service
public class ZohoApiClientSalesOrderService {
	
	
		@Autowired
		private ZohoConnectorProperties connProperties;
		

	    /**
	     * Path for End-Point to search for request SalesOrder 
	     */
	    private static final String END_POINT_GET_SALES_ORDER = "/salesorders/%s";
	    
	    /**
	     * Path for End-Point to search for request all SalesOrders 
	     */
	    private static final String END_POINT_GET_ALL_SALES_ORDER = "/salesorders";
	    

	    public ZohoResponse fetchAllSalesOrder(String organizationId, String authKey) {
	        
	        String url = connProperties.getApiBaseUrlInventory().concat(END_POINT_GET_ALL_SALES_ORDER).concat("?organization_id=" + organizationId);
	        
	        HttpClient client = HttpClient.newHttpClient();
	        
	        HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(url))
	            .header("Content-Type", "application/json")
	            .header("Authorization", "Zoho-oauthtoken " + authKey)
	            .GET()
	            .build();

	        return sendRequestAndGetMappedResult(client, request, "\"salesorders\"");

	    }
	   
	    public ZohoResponse fetchSalesOrderById(String organizationId, String salesOrderId, String authKey) {
	    	
	        String url = String.format( connProperties.getApiBaseUrlInventory().concat(END_POINT_GET_SALES_ORDER), salesOrderId)
	        				.concat("?organization_id=" + organizationId);
	        
	        HttpClient client = HttpClient.newHttpClient();
	        
	        HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(url))
	            .header("Content-Type", "application/json")
	            .header("Authorization", "Zoho-oauthtoken " + authKey)
	            .GET()
	            .build();

	        return sendRequestAndGetMappedResult(client, request, "\"salesorder\"");

	    }
	    
	    public ZohoResponse setStatus(String organizationId, String salesOrderId, String authKey, String status) {
	    		
	    	String url = String
			    			.format( connProperties.getApiBaseUrlInventory().concat(END_POINT_GET_SALES_ORDER), salesOrderId)
			    			.concat("/status/").concat(status)
		    				.concat("?organization_id=" + organizationId);
	    	
	    	
	    	HttpRequest request = HttpRequest.newBuilder()
		            .uri(URI.create(url))
		            .header("Content-Type", "application/json")
		            .header("Authorization", "Zoho-oauthtoken " + authKey)
		            .GET()
		            .build();
	    	
	    	HttpClient client = HttpClient.newHttpClient();
	    	
	    	try {
				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
				return getObjectMapper(response.body(), "salesorder");
	    	
	    	} catch (IOException | InterruptedException e) {
	    		throw new RuntimeException(e);
			}
	    	
	    }
	    
	    
	    public ZohoResponse setSubStatus(String organizationId, String salesOrderId, String authKey, String substatus) {
    		
	    	String url = String
			    			.format( connProperties.getApiBaseUrlInventory().concat(END_POINT_GET_SALES_ORDER), salesOrderId)
			    			.concat("/substatus/").concat(substatus)
		    				.concat("?organization_id=" + organizationId);
	    	
	    	
	    	HttpRequest request = HttpRequest.newBuilder()
		            .uri(URI.create(url))
		            .header("Content-Type", "application/json")
		            .header("Authorization", "Zoho-oauthtoken " + authKey)
		            .POST(HttpRequest.BodyPublishers.noBody())
		            .build();
	    	
	    	HttpClient client = HttpClient.newHttpClient();
	    	
	    	try {
				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
				return getObjectMapper(response.body(), "salesorder");
	    	
	    	} catch (IOException | InterruptedException e) {
	    		throw new RuntimeException(e);
			}
	    	
	    }

	    private ZohoResponse sendRequestAndGetMappedResult(HttpClient client, HttpRequest request, String node) {
	        HttpResponse<String> response;
	        try {
	            response = client.send(request, HttpResponse.BodyHandlers.ofString());
	            
	                String responseBody = response.body();
	    
	                return getObjectMapper(responseBody, node);
	            
	        } catch (IOException | InterruptedException e) {
	            throw new RuntimeException(e);
	        }
	    }

	    private ZohoResponse getObjectMapper(String responseBody, String node) throws JsonProcessingException, JsonMappingException {
	        ObjectMapper objectMapper = JsonMapper
	            .builder()
	            .addModule(new JavaTimeModule())
	            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
	            .build();
	        
	        String body = responseBody.replace(node, "\"body\"");
   
	        ZohoResponse apiResponse = objectMapper.readValue(body, ZohoResponse.class);
	        return apiResponse;
	    }

}

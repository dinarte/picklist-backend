package com.quebecteh.modules.commons.clients.api.trackpod.service;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.quebecteh.modules.commons.clients.api.trackpod.exception.ClientRequestException;
import com.quebecteh.modules.commons.clients.api.trackpod.model.RouteDTO;

/**
 * Implements the service responsible for recovering requests from routes through a client HTTP consuming the trackpod API Endpoint
 * @author Dinarte Alves - dinarte@gmail.com
 * @since 1.0.0
 */

@Service
public class RouteApiClientServiceImpl implements RouteApiClientService {

    /**
     * Defines the track-pod API address.
     */
    @Value("${bde-trackpod.client.api.url}")
    private String API_URL;
    
    /**
     * Key api for authentication in the Track-Pod API.
     */
    @Value("${bde-trackpod.client.api.key}")
    private String API_KEY;

    /**
     * Path for End-Point to search for requests by date.
     */
    private static final String END_POINT_GET_ORDERS_BY_DATE = "/Route/Date/%s";
    


    /**
     * Implements the search for routes from a specified date.
     * @param Date a string in "YYYY-MM-DD format, example: 2024-08-27".
     * @return List <OrderDTO>: a list of routes.
     * @throws Exception
     */
    @Override
    public List<RouteDTO> fetchRoutesByDate(String date) throws ClientRequestException {
        
        String url = String.format( API_URL.concat(END_POINT_GET_ORDERS_BY_DATE), date);
        
        HttpClient client = HttpClient.newHttpClient();
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("accept", "text/plain")
            .header("X-API-KEY", API_KEY)
            .GET()
            .build();

        return sendRequestAndGetMappedResult(client, request);

    }

    private List<RouteDTO> sendRequestAndGetMappedResult(HttpClient client, HttpRequest request) {
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
    
                return getObjectMapper(responseBody);
            } else {
                throw new ClientRequestException(response.statusCode(), API_URL);
            }
        } catch (IOException | InterruptedException e) {
            throw new ClientRequestException(e, API_URL );
        }
    }

    private List<RouteDTO> getObjectMapper(String responseBody) throws JsonProcessingException, JsonMappingException {
        ObjectMapper objectMapper = JsonMapper
            .builder()
            .addModule(new JavaTimeModule())
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .build();
            List<RouteDTO> listDto = objectMapper.readValue(responseBody, new TypeReference<List<RouteDTO>>() {});
        return listDto;
    }
}
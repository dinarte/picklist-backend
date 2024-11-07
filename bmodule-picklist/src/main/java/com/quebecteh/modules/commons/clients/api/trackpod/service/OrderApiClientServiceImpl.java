package com.quebecteh.modules.commons.clients.api.trackpod.service;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
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
import com.quebecteh.modules.commons.clients.api.trackpod.model.OrderDTO;
import com.quebecteh.modules.commons.clients.api.trackpod.model.TrackPodApiStatusResponse;
import com.quebecteh.modules.commons.clients.api.trackpod.model.TrackPodNewOrderDTO;

/**
 * Implements the service responsible for recovering requests from orders through a client HTTP consuming the trackpod API Endpoint
 * @author Dinarte Alves - dinarte@gmail.com
 * @since 1.0.0
 */

@Service
public class OrderApiClientServiceImpl implements OrderApiClientService {

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
    private static final String END_POINT_GET_ORDERS_BY_DATE = "/Order/Date/%s";
    
    /**
     * Path for End-Point to search for requests by number.
     */
    private static final String END_POINT_GET_ORDER_BY_NUMBER = "/Order/Number/%s";
    
    
    /**
     * Path for End-Point to search for requests by number.
     */
    private static final String END_POINT_GET_ORDER_BY_ID = "/Order/Id/%s";
    
    /**
     * Path for End-Point to POST requests.
     */
    
    private static final String END_POINT_POST_ORDER = "/Order";
    
    
    /**
     * Fetches an order from TrackPod using its unique number.
     * This method sends a GET request to the TrackPod API to retrieve order details based on the given order number.
     *
     * @param number the unique identifier of the order to be fetched.
     * @return {@link OrderDTO} representing the details of the fetched order.
     * @throws ClientRequestException if there is an error processing the client request, such as an invalid response.
     * @throws IOException if an I/O error occurs during the communication with the TrackPod API.
     * @throws InterruptedException if the request is interrupted.
     */
    @Override
    public OrderDTO fetchOrderByNumber(String number) throws ClientRequestException, IOException, InterruptedException {
        
        String url = String.format( API_URL.concat(END_POINT_GET_ORDER_BY_NUMBER), number);
        
        HttpClient client = HttpClient.newHttpClient();
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("accept", "text/plain")
            .header("X-API-KEY", API_KEY)
            .GET()
            .build();
        
        HttpResponse<String> response = sendRequest(client, request);
        OrderDTO trackPodOrder = getOneObjectMapped(response.body());
        
        return trackPodOrder;

    }
    
    /**
     * Fetches an order from TrackPod using its unique number.
     * This method sends a GET request to the TrackPod API to retrieve order details based on the given order number.
     *
     * @param number the unique identifier of the order to be fetched.
     * @return {@link OrderDTO} representing the details of the fetched order.
     * @throws ClientRequestException if there is an error processing the client request, such as an invalid response.
     * @throws IOException if an I/O error occurs during the communication with the TrackPod API.
     * @throws InterruptedException if the request is interrupted.
     */
    @Override
    public OrderDTO fetchOrderById(String id) throws ClientRequestException, IOException, InterruptedException {
        
        String url = String.format( API_URL.concat(END_POINT_GET_ORDER_BY_ID), id);
        
        HttpClient client = HttpClient.newHttpClient();
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("accept", "text/plain")
            .header("X-API-KEY", API_KEY)
            .GET()
            .build();
        
        HttpResponse<String> response = sendRequest(client, request);
        OrderDTO trackPodOrder = getOneObjectMapped(response.body());
        
        return trackPodOrder;

    }
    
    
    
    @Override
    public TrackPodApiStatusResponse create(TrackPodNewOrderDTO trackPodOrder) throws ClientRequestException, IOException, InterruptedException {
        
        ObjectMapper objectMapper = JsonMapper
                .builder()
                .addModule(new JavaTimeModule())
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .build();
        String requestBody = objectMapper.writeValueAsString(trackPodOrder);

        String url = API_URL.concat(END_POINT_POST_ORDER);
        
        HttpClient client = HttpClient.newHttpClient();
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .header("X-API-KEY", API_KEY)
            .POST(BodyPublishers.ofString(requestBody))
            .build();
        HttpResponse<String> response =  sendRequest(client, request);
        
        TrackPodApiStatusResponse trackPodeResponse = (TrackPodApiStatusResponse) getObjectMaped(response.body(), new TypeReference<TrackPodApiStatusResponse>() {});
        return trackPodeResponse;

    }
    
    private HttpResponse<String> sendRequest(HttpClient client, HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;   
    }
    


    /**
     * Implements the search for orders from a specified date.
     * @param Date a string in "YYYY-MM-DD format, example: 2024-08-27".
     * @return List <OrderDTO>: a list of orders.
     * @throws Exception
     */
    @Override
    public List<OrderDTO> fetchOrdersByDate(String date) throws ClientRequestException {
        
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
    
    private List<OrderDTO> sendRequestAndGetMappedResult(HttpClient client, HttpRequest request) {
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
    
                return getListObjectsMapped(responseBody);
            } else {
                throw new ClientRequestException(response.statusCode(), API_URL);
            }
        } catch (IOException | InterruptedException e) {
            throw new ClientRequestException(e, API_URL );
        }
    }

    private List<OrderDTO> getListObjectsMapped(String responseBody) throws JsonProcessingException, JsonMappingException {
        ObjectMapper objectMapper = JsonMapper
            .builder()
            .addModule(new JavaTimeModule())
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .build();
        return objectMapper.readValue(responseBody, new TypeReference<List<OrderDTO>>() {});
    }
    
    private OrderDTO getOneObjectMapped(String responseBody) throws JsonProcessingException, JsonMappingException {
        ObjectMapper objectMapper = JsonMapper
            .builder()
            .addModule(new JavaTimeModule())
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .build();
        return objectMapper.readValue(responseBody, new TypeReference<OrderDTO>() {});
    }
    
    private Object getObjectMaped(String responseBody, TypeReference<?> typeReferece) throws JsonProcessingException, JsonMappingException {
        ObjectMapper objectMapper = JsonMapper
            .builder()
            .addModule(new JavaTimeModule())
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .build();
        return objectMapper.readValue(responseBody, typeReferece);
    }
}
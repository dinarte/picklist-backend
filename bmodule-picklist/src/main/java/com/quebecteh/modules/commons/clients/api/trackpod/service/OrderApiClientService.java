package com.quebecteh.modules.commons.clients.api.trackpod.service;

import java.io.IOException;
import java.util.List;

import com.quebecteh.modules.commons.clients.api.trackpod.exception.ClientRequestException;
import com.quebecteh.modules.commons.clients.api.trackpod.model.OrderDTO;
import com.quebecteh.modules.commons.clients.api.trackpod.model.TrackPodApiStatusResponse;
import com.quebecteh.modules.commons.clients.api.trackpod.model.TrackPodNewOrderDTO;

/**
 * Interface for the TrackPod API client service that allows the creation and retrieval of orders.
 * This interface provides methods to interact with the TrackPod API, including creating new orders and fetching existing orders.
 */
public interface OrderApiClientService {

    /**
     * Creates a new order in the TrackPod system.
     *
     * @param order {@link TrackPodNewOrderDTO} object containing the information of the order to be created.
     * @return {@link TrackPodApiStatusResponse} containing a track-pod default response for requests (POST, PUT, DELETE), which can be used to check the status of the request.
     * @throws ClientRequestException if there is an error processing the client request.
     * @throws IOException if an I/O error occurs during communication with the API.
     * @throws InterruptedException if the request is interrupted.
     */
	TrackPodApiStatusResponse create(TrackPodNewOrderDTO order) throws ClientRequestException, IOException, InterruptedException;

    /**
     * Searches for orders created from a specified date.
     *
     * @param date a string in "YYYY-MM-DD" format, for example: "2024-08-27".
     * @return List of {@link OrderDTO}: a list of orders matching the specified date.
     * @throws ClientRequestException if there is an error processing the client request.
     */
    List<OrderDTO> fetchOrdersByDate(String date) throws ClientRequestException;

    /**
     * Fetches an order by its unique number.
     *
     * @param number the unique identifier of the order.
     * @return {@link OrderDTO} representing the order with the specified number.
     * @throws ClientRequestException if there is an error processing the client request.
     * @throws InterruptedException 
     * @throws IOException 
     */
    OrderDTO fetchOrderByNumber(String number) throws ClientRequestException, IOException, InterruptedException;

	/**
	 * Fetches an order from TrackPod using its unique Id.
	 * This method sends a GET request to the TrackPod API to retrieve order details based on the given order id.
	 *
	 * @param number the unique identifier of the order to be fetched.
	 * @return {@link OrderDTO} representing the details of the fetched order.
	 * @throws ClientRequestException if there is an error processing the client request, such as an invalid response.
	 * @throws IOException if an I/O error occurs during the communication with the TrackPod API.
	 * @throws InterruptedException if the request is interrupted.
	 */
	OrderDTO fetchOrderById(String id) throws ClientRequestException, IOException, InterruptedException;

}

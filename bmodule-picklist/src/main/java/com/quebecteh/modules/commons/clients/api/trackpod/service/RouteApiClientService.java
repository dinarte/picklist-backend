package com.quebecteh.modules.commons.clients.api.trackpod.service;

import java.util.List;

import com.quebecteh.modules.commons.clients.api.trackpod.exception.ClientRequestException;
import com.quebecteh.modules.commons.clients.api.trackpod.model.RouteDTO;

public interface RouteApiClientService {

    /**
     * Search for routes from a specified date.
     * @param Date a string in "YYYY-MM-DD format, example: 2024-08-27"
     * @return List<OrderDTO>: a list of routes
     * @throws ClientRequestException
     */
    List<RouteDTO> fetchRoutesByDate(String date) throws ClientRequestException;

}
package com.quebecteh.modules.commons.clients.api.zoho.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ZohoResponse {

    private int code;

    private String message;

    private Object body;
    
    @JsonProperty("page_context")
    private ZohoApiResponsePageContextDTO pageContext;

}
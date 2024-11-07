package com.quebecteh.modules.commons.clients.api.zoho.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quebecteh.modules.commons.connector.model.dto.OrganizationDTO;

import lombok.Data;

@Data 
public class ZohoApiResponseDTO {
	
	private int code;
    
	private String message;
   
    private List<OrganizationDTO> organizations;
    
    @JsonProperty("page_context")
    private ZohoApiResponsePageContextDTO pageContext;
}

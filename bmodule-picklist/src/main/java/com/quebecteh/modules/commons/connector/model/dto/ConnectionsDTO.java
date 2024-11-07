package com.quebecteh.modules.commons.connector.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConnectionsDTO {
	
	private String product;
	
	private String scope;
	
	private String status;
	
	private boolean active;
	
	private ApiEndPointDTO[] endPoints;
	

}

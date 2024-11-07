package com.quebecteh.modules.commons.connector.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiEndPointDTO {
	
	private String description;
	
	private String endPointId;
	
	private String url;
	
	private String method;
}

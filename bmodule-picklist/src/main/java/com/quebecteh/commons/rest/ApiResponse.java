package com.quebecteh.commons.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
	
	protected int code;
	protected String messageId;
	protected String message;
	protected T body;
	
	public String getResult() {
		return "Sucess";
	}

}

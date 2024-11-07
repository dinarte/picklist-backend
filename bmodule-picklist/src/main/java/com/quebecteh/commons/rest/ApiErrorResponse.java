package com.quebecteh.commons.rest;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ApiErrorResponse<T> extends ApiResponse<T> {
	
	public ApiErrorResponse(int code, String messageId, String message, T data) {
		super(code, messageId, message, data);
	}
	
	@Override
	public String getResult() {
		return "Error";
	}
	
}

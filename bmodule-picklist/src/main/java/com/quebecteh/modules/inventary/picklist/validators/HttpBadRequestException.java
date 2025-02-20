package com.quebecteh.modules.inventary.picklist.validators;

public class HttpBadRequestException extends HttpException {

	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_MESSAGE = "Bed request exception.";
	
	public HttpBadRequestException() {
		super(DEFAULT_MESSAGE);
	}
	
	public HttpBadRequestException(Object detail, String detailId) {
		super(DEFAULT_MESSAGE);
		this.detail = detail; 
		this.detailId = detailId;
	}

	@Override
	public int getCode() {
		return 400;
	}

	@Override
	public String getMessageId() {
		return "bed-request-exception";
	}
		

}

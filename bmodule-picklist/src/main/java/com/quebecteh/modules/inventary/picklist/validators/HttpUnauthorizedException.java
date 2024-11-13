package com.quebecteh.modules.inventary.picklist.validators;

public class HttpUnauthorizedException extends HttpException {

	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_MESSAGE = "Unauthorized";
	
	public HttpUnauthorizedException() {
		super(DEFAULT_MESSAGE);
	}
	
	public HttpUnauthorizedException(Object detail, String detailId) {
		super(DEFAULT_MESSAGE);
		this.detail = detail; 
		this.detailId = detailId;
	}

	@Override
	public int getCode() {
		return 401;
	}

	@Override
	public String getMessageId() {
		return "unauthorized-exception";
	}
		

}

package com.quebecteh.modules.inventary.picklist.validators;

public class HttpResouceNotFoundException extends HttpException {


	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_MESSAGE = "The resource you tried to access cannot be found.";
	
	public HttpResouceNotFoundException() {
		super(DEFAULT_MESSAGE);
	}
	
	public HttpResouceNotFoundException(String detail, String detailId) {
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
		return "not-found-exception";
	}
		
		

}

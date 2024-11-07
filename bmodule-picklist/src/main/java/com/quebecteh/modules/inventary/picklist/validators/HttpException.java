package com.quebecteh.modules.inventary.picklist.validators;

public abstract class HttpException extends Exception implements ExceptionDetail {

	private static final long serialVersionUID = 1L;
	
	protected String detail;
	protected String detailId;

	public HttpException(String message) {
		super(message);		
	}
	
	public HttpException(String message, String detail, String detailId) {
		this.detail = detail;
		this.detailId = detailId;
	}
	
	public HttpException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public abstract int getCode();
	
	public abstract String getMessageId();

	@Override
	public String getDetail() {
		return detail;
	}

	@Override
	public String getDetailId() {
		return detailId;
	}
	
	
	
}

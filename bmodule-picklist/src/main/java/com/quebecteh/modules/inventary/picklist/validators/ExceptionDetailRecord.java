package com.quebecteh.modules.inventary.picklist.validators;

public record ExceptionDetailRecord(Object detail, String detailId) implements ExceptionDetail{

	@Override
	public Object getDetail() {
		return detail;
	}

	@Override
	public String getDetailId() {
		return detailId;
	}

}

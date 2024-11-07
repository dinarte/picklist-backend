package com.quebecteh.modules.inventary.picklist.validators;

public record ExceptionDetailRecord(String detail, String detailId) implements ExceptionDetail{

	@Override
	public String getDetail() {
		return detail;
	}

	@Override
	public String getDetailId() {
		return detailId;
	}

}

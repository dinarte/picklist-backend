package com.quebecteh.modules.inventary.picklist.validators;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class ApiValidationFieldException extends RuntimeException {
	
	

	@Getter
	private List<ValidationError> errors = new ArrayList<ValidationError>();
	
	private static final long serialVersionUID = 1L;
	

	public ApiValidationFieldException(ValidationError errors) {
		super();
		this.errors.add(errors);
	}
	
	public ApiValidationFieldException(List<ValidationError> errors) {
		super();
		this.errors = errors;
	}
		
	
	
	
}

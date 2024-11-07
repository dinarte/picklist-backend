package com.quebecteh.modules.inventary.picklist.validators;

import lombok.Builder;

@Builder
public record ValidationError(
	    String defaultMessage,
	    String defaultMessageId,
	    String objectName,
	    String field,
	    String code,
	    String rejectedValue
) {	
	public void throwsExcpeion() {
		
		throw new ApiValidationFieldException(this);
	}
}
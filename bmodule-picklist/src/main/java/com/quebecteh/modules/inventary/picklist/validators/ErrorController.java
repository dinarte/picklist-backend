package com.quebecteh.modules.inventary.picklist.validators;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.SneakyThrows;

@RestController
public class ErrorController {
	
	@SneakyThrows
	@GetMapping("/error/tenant/{tenantId}")
	public void handleTentenIdError( @PathVariable("tenantId") String tenantId) {
		throw new HttpResouceNotFoundException("Tenant ID '"+tenantId+"' not found", "tenant-id-not-found");
	}
	
}

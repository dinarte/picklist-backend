package com.quebecteh.modules.inventary.picklist.validators;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.quebecteh.commons.rest.ApiErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;

@RestController
public class ErrorController {

	
	@GetMapping("/error")
	public ResponseEntity<?> error(HttpServletRequest request) {
		
		 var responseMap = new HashMap<String, Object>();
		 
		 responseMap.putAll( request.getParameterMap() );
		 request.getAttributeNames().asIterator().forEachRemaining( key -> responseMap.put(key, request.getAttribute(key)));
		 request.getHeaderNames().asIterator().forEachRemaining( key -> responseMap.put(key, request.getHeader(key)));
		 
		 ApiErrorResponse<Map<String, Object>> response = new ApiErrorResponse<>(500, "error", "Error", responseMap); 
		 
		 return new ResponseEntity<>(response, HttpStatus.valueOf(500));
		 
	}
	
	
	@SneakyThrows
	@GetMapping("/error/tenant/{tenantId}")
	public void handleTentenIdError( @PathVariable("tenantId") String tenantId) {
		throw new HttpResouceNotFoundException("Tenant ID '"+tenantId+"' not found", "tenant-id-not-found");
	}
	
}

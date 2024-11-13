package com.quebecteh.modules.commons.connector.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.quebecteh.modules.commons.clients.api.zoho.connector.ZohoConnectorProperties;
import com.quebecteh.modules.commons.connector.model.dto.ApiEndPointDTO;
import com.quebecteh.modules.commons.connector.model.dto.ConnectionsDTO;
import com.quebecteh.modules.inventary.picklist.interceptors.RequiredTenatantId;
import com.quebecteh.modules.inventary.picklist.model.domain.PickListUserAuth;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;


@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RestController
@RequiredTenatantId
public class ConnectionsController {
		
	
	private final PickListUserAuth auth;
	
	final ZohoConnectorProperties connectoProperties;
	
	
	@SneakyThrows
	@GetMapping("/{tenantId}/connections")
	public ResponseEntity<ConnectionsDTO[]> index( @PathVariable("tenantId") String tenantId){
		
		//if (tenantService.countByTenantId(tenantId) == 0 )
			//throw new HttpResouceNotFoundException("Tenant ID '"+tenantId+"' not found", "tenant-id-not-found");
		
		
		ApiEndPointDTO[] zohoEndPoins = {
				new ApiEndPointDTO("Connect to Zoho Inventary", "connect-zoho-inventary", "/zoho/" +tenantId + "/auth", "GET")
		};
	
		
		var zohoInventaryConnStatus = "Connected";
		if (auth == null || auth.getId() == null) {
			zohoInventaryConnStatus = "Not connected";
		} else if (!auth.getTenantId().equals(tenantId)) {
			zohoInventaryConnStatus = "Not connected";
		}
		
		ConnectionsDTO zohoConn = new ConnectionsDTO("Zoho Inventary", connectoProperties.getScope(), zohoInventaryConnStatus, true, zohoEndPoins);
		
		ConnectionsDTO[] connections = {zohoConn};
		
		return ResponseEntity.ok(connections);
	}
	

}

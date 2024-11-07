package com.quebecteh.modules.inventary.picklist.model.domain;

import org.springframework.stereotype.Component;

import com.quebecteh.modules.commons.connector.model.domain.ZohoConnection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickListUserAuth {

	private String id;
	
	private String  userName;
	
	private String  userEmail;
	
	private String  userOriginId;
	
	private String tenantId;
	
	private ZohoConnection conn;

}

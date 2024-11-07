package com.quebecteh.modules.commons.connector.model.domain;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder 
@Entity
@Table(name = "zoho_connection", schema = "bmodules-picklist")
public class ZohoConnection {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String appName;
	
	private String  accesToken;
	
	private String 	refreshToken;
	
	private String scope;
	
	private LocalDateTime createdIn;
	
	private LocalDateTime renewdIn;
	
	private LocalDateTime expireIn;
	
	private String userId;
	
	private String userName;
	
	private String userEmail;
	
	@JsonManagedReference
	@OneToMany(mappedBy = "connection")
	private List<ZohoOrganization> organizations;
	
	private String tenantId;
	
}

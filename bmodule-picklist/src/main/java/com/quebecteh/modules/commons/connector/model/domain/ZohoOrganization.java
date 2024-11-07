package com.quebecteh.modules.commons.connector.model.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
@Entity
@Table(name = "zoho_organization", schema = "bmodules-picklist")
public class ZohoOrganization {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    
    private String organizationId;
    
    @JsonBackReference
    @ManyToOne
    private ZohoConnection connection;
    
    private String name;
    
    private String contactName;
    
    private String email;
    
    private String country;
    
    private String languageCode;
    
    private String timeZone;
    
    private String phone;
    
    private String tenantId;
}

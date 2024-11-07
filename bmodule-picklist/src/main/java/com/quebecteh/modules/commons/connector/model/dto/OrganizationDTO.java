package com.quebecteh.modules.commons.connector.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) 
public class OrganizationDTO {
    
    @JsonProperty("organization_id")
    private String organizationId;
    
    private String name;
    
    @JsonProperty("contact_name")
    private String contactName;
    
    private String email;
    
    private String country;
    
    @JsonProperty("language_code")
    private String languageCode;
    
    @JsonProperty("time_zone")
    private String timeZone;
    
    private String phone;
    
    private boolean inUse;
    
    private ApiEndPointDTO[] endPoints;
}

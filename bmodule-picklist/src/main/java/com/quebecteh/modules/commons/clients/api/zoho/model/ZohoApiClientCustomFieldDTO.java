package com.quebecteh.modules.commons.clients.api.zoho.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ZohoApiClientCustomFieldDTO {

    @JsonProperty("custom_field_id")
    private Long customFieldId;

    @JsonProperty("index")
    private int index;

    @JsonProperty("label")
    private String label;

    @JsonProperty("value")
    private String value;

}
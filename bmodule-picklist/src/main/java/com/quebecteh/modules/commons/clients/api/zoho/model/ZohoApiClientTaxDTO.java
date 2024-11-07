package com.quebecteh.modules.commons.clients.api.zoho.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ZohoApiClientTaxDTO {

    @JsonProperty("tax_name")
    private String taxName;

    @JsonProperty("tax_amount")
    private BigDecimal taxAmount;

}
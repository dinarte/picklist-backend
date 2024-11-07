package com.quebecteh.modules.commons.clients.api.zoho.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ZohoApiClientLineItemDTO {

    @JsonProperty("item_id")
    private Long itemId;

    @JsonProperty("line_item_id")
    private Long lineItemId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("item_order")
    private int itemOrder;

    @JsonProperty("bcy_rate")
    private BigDecimal bcyRate;

    @JsonProperty("rate")
    private BigDecimal rate;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("quantity_invoiced")
    private int quantityInvoiced;

    @JsonProperty("quantity_packed")
    private int quantityPacked;

    @JsonProperty("quantity_shipped")
    private int quantityShipped;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("tax_id")
    private Long taxId;

    @JsonProperty("tds_tax_id")
    private String tdsTaxId;

    @JsonProperty("tax_name")
    private String taxName;

    @JsonProperty("tax_type")
    private String taxType;

    @JsonProperty("tax_percentage")
    private int taxPercentage;

    @JsonProperty("item_total")
    private BigDecimal itemTotal;

    @JsonProperty("is_invoiced")
    private boolean isInvoiced;

    @JsonProperty("image_id")
    private Long imageId;

    @JsonProperty("image_name")
    private String imageName;

    @JsonProperty("image_type")
    private String imageType;

    @JsonProperty("warehouse_id")
    private Long warehouseId;

    @JsonProperty("hsn_or_sac")
    private int hsnOrSac;

    @JsonProperty("sat_item_key_code")
    private int satItemKeyCode;

    @JsonProperty("unitkey_code")
    private String unitKeyCode;

}
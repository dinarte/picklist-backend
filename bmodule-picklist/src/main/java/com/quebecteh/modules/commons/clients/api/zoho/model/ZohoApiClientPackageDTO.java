package com.quebecteh.modules.commons.clients.api.zoho.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ZohoApiClientPackageDTO {

    @JsonProperty("package_id")
    private Long packageId;

    @JsonProperty("package_number")
    private String packageNumber;

    @JsonProperty("status")
    private String status;

    @JsonProperty("detailed_status")
    private String detailedStatus;

    @JsonProperty("status_message")
    private String statusMessage;

    @JsonProperty("shipment_id")
    private Long shipmentId;

    @JsonProperty("shipment_number")
    private String shipmentNumber;

    @JsonProperty("shipment_status")
    private String shipmentStatus;

    @JsonProperty("carrier")
    private String carrier;

    @JsonProperty("service")
    private String service;

    @JsonProperty("tracking_number")
    private String trackingNumber;

    @JsonProperty("shipment_date")
    private LocalDate shipmentDate;

    @JsonProperty("delivery_days")
    private int deliveryDays;

    @JsonProperty("delivery_guarantee")
    private boolean deliveryGuarantee;

}
package com.quebecteh.modules.commons.clients.api.zoho.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ZohoApiClientInvoiceDTO {

    @JsonProperty("invoice_id")
    private Long invoiceId;

    @JsonProperty("invoice_number")
    private String invoiceNumber;

    @JsonProperty("status")
    private String status;

    @JsonProperty("date")
    private LocalDate date;

    @JsonProperty("due_date")
    private LocalDate dueDate;

    @JsonProperty("total")
    private BigDecimal total;

    @JsonProperty("balance")
    private BigDecimal balance;

}
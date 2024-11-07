package com.quebecteh.modules.commons.clients.api.zoho.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ZohoApiClientDocumentDTO {

    @JsonProperty("can_send_in_mail")
    private boolean canSendInMail;

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("file_type")
    private String fileType;

    @JsonProperty("file_size_formatted")
    private String fileSizeFormatted;

    @JsonProperty("attachment_order")
    private int attachmentOrder;

    @JsonProperty("document_id")
    private Long documentId;

    @JsonProperty("file_size")
    private int fileSize;

    // Getters and Setters (omitted for brevity)
}
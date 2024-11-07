package com.quebecteh.modules.commons.clients.api.zoho.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ZohoApiClientContactPersonDTO {

    private String contactPersonId;
    private String contactPersonEmail;
    private String firstName;
    private String lastName;
    private String mobile;
    private ZoroApiCLientCommunicationPreference communicationPreference;

    public ZohoApiClientContactPersonDTO() {
    }

    @JsonCreator
    public ZohoApiClientContactPersonDTO(
            @JsonProperty("contact_person_id") String contactPersonId,
            @JsonProperty("contact_person_email") String contactPersonEmail,
            @JsonProperty("first_name") String firstName,
            @JsonProperty("last_name") String lastName,
            @JsonProperty("mobile") String mobile,
            @JsonProperty("communication_preference") ZoroApiCLientCommunicationPreference communicationPreference) {
        this.contactPersonId = contactPersonId;
        this.contactPersonEmail = contactPersonEmail;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile = mobile;
        this.communicationPreference = communicationPreference;
    }

 
}

@Data
class ZoroApiCLientCommunicationPreference {

    private boolean isEmailEnabled;
    private boolean isSmsEnabled;
    private boolean isWhatsappEnabled;

    // Construtor padrão
    public ZoroApiCLientCommunicationPreference() {
    }

    @JsonCreator
    public ZoroApiCLientCommunicationPreference(
            @JsonProperty("is_email_enabled") boolean isEmailEnabled,
            @JsonProperty("is_sms_enabled") boolean isSmsEnabled,
            @JsonProperty("is_whatsapp_enabled") boolean isWhatsappEnabled) {
        this.isEmailEnabled = isEmailEnabled;
        this.isSmsEnabled = isSmsEnabled;
        this.isWhatsappEnabled = isWhatsappEnabled;
    }
}
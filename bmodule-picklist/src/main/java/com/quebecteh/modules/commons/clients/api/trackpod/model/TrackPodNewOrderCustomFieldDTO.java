package com.quebecteh.modules.commons.clients.api.trackpod.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TrackPodNewOrderCustomFieldDTO {

    @JsonProperty("Id")
    private String id;

    @JsonProperty("Value")
    private String value;
}

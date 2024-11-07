package com.quebecteh.modules.commons.clients.api.trackpod.model;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Represents the response status from the TrackPod API.
 * This class contains information about the status of a request,
 * such as the status code, title, and detailed message.
 */
@Data
public class TrackPodApiStatusResponse {

    /** The status code of the response. */
    @JsonProperty("Status")
    private int status;

    /** The title of the response, which provides a summary of the status. */
    @JsonProperty("Title")
    private String title;

    /** The detail message of the response, providing additional information. */
    @JsonProperty("Detail")
    private String detail;

}
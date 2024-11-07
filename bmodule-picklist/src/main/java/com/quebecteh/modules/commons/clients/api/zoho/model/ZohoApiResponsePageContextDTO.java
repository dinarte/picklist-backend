package com.quebecteh.modules.commons.clients.api.zoho.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ZohoApiResponsePageContextDTO {

    @JsonProperty("page")
    private int page;

    @JsonProperty("per_page")
    private int perPage;

    @JsonProperty("has_more_page")
    private boolean hasMorePage;

    @JsonProperty("report_name")
    private String reportName;

    @JsonProperty("applied_filter")
    private String appliedFilter;

    @JsonProperty("custom_fields")
    private List<Object> customFields;

    @JsonProperty("sort_column")
    private String sortColumn;

    @JsonProperty("sort_order")
    private String sortOrder;

}
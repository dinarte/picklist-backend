package com.quebecteh.modules.commons.clients.api.trackpod.model;

import lombok.Data;

@Data
public class CustomFieldDTO {
    
    private Long localID;
    private String id;
    private String label;
    private String value;
}

package com.quebecteh.modules.migrators.controller;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MigrationConfiguration {
	
	String sourceAppName;
	String sourceEntity;
	String sourceFieldId;
	String destinationApp;
	String destinationResource;
	String destinationEntity;
	String destinationFieldId;
	String tenantId;

}

package com.quebecteh.modules.migrators.domain.sourceMapping;

import com.quebecteh.modules.migrators.domain.SourceModel;

import lombok.Data;

@Data
public class Mapping {
	
	private String name;
	
	private SourceModelRelaction origin;
	
	private SourceModel destination; 
	
	

}

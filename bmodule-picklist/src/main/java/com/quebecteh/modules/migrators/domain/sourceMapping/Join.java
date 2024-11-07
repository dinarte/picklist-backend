package com.quebecteh.modules.migrators.domain.sourceMapping;

import com.quebecteh.modules.migrators.domain.SourceModel;

import lombok.Data;

@Data
public class Join {
	
	public static String ONE_TO_MANY= "OneToMmany";
	public static String MANY_TO_ONE = "MmanyToOne";
	
	private String type = ONE_TO_MANY;
	private String joinFieldName;
	private String idFieldName;
	
	private SourceModel model;
	
	
	public void parent(SourceModel model, String idFieldName, String joinFieldName) {
		
		this.type = ONE_TO_MANY;
		
		this.model = model;
		this.idFieldName = idFieldName;
		this.joinFieldName = joinFieldName;
		
	}

}

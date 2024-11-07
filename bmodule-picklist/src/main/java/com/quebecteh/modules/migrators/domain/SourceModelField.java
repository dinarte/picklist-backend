package com.quebecteh.modules.migrators.domain;

public interface SourceModelField {

	Long getId();

	Integer getLength();

	SourceModel getModel();

	String getName();

	String getTenantId();

	String getType();
	
	Boolean getRequired();
	
	String getDescription();

	void setId(Long id);

	void setLength(Integer length);

	void setModel(SourceModel model);

	void setName(String name);

	void setTenantId(String tenantId);

	void setType(String type);
	
	void setRequired(Boolean required);

}
package com.quebecteh.modules.migrators.domain;

public interface SourceModel {

	String getAppId();

	Long getId();

	String getName();

	int hashCode();

	void setAppId(String appId);

	void setId(Long id);

	void setName(String name);

}
package com.quebecteh.commons.multitenancy;

public interface BusinessRulesEventTriggers<T> {
	
	public void beforeCreate(T obj);
	
	public void beforeUpdata(T oldStateObj, T newStateObj);
	
	public void beforeDelete(T obj);

}

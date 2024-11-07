package com.quebecteh.commons.multitenancy;

import java.util.List;

import com.quebecteh.commons.service.CrudService;

public interface MultiTenancyService<T, ID> extends CrudService<T, ID> {

	public List<T> findByTenantId(String tenantId);
	
	public Long countByTenantId(String tenantId);

}
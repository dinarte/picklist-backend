package com.quebecteh.commons.multitenancy;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface MultiTenancyCrudRepository<T, ID> extends CrudRepository<T, ID> {
	
	List<T> findByTenantId(@Param("tenantId") String tenantId);
	
	Long countByTenantId(@Param("tenantId") String tenantId);

}

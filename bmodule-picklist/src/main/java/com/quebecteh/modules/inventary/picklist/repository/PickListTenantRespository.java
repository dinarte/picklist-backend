package com.quebecteh.modules.inventary.picklist.repository;

import com.quebecteh.commons.multitenancy.MultiTenancyCrudRepository;
import com.quebecteh.modules.inventary.picklist.model.domain.PickListTenant;


public interface PickListTenantRespository extends MultiTenancyCrudRepository<PickListTenant, Long>{
	
	
}

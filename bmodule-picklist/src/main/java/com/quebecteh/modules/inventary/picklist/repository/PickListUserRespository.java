package com.quebecteh.modules.inventary.picklist.repository;

import com.quebecteh.commons.multitenancy.MultiTenancyCrudRepository;
import com.quebecteh.modules.inventary.picklist.model.domain.PickListUser;

public interface PickListUserRespository extends MultiTenancyCrudRepository<PickListUser, Long>{
	
	
}

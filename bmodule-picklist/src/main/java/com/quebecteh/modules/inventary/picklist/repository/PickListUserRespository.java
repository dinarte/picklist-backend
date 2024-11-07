package com.quebecteh.modules.inventary.picklist.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.quebecteh.commons.multitenancy.MultiTenancyCrudRepository;
import com.quebecteh.modules.inventary.picklist.model.domain.PickListUser;

@RepositoryRestResource()
public interface PickListUserRespository extends MultiTenancyCrudRepository<PickListUser, Long>{
	
	
}

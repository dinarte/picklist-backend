package com.quebecteh.modules.inventary.picklist.repository;

import com.quebecteh.commons.multitenancy.MultiTenancyCrudRepository;
import com.quebecteh.modules.inventary.picklist.model.domain.PickListItem;


public interface PickListItemRepository extends MultiTenancyCrudRepository<PickListItem, Long>{
	
}

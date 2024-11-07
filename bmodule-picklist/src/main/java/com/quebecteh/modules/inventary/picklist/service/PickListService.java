package com.quebecteh.modules.inventary.picklist.service;

import java.util.List;

import com.quebecteh.commons.multitenancy.MultiTenancyService;
import com.quebecteh.modules.inventary.picklist.model.domain.PickList;
import com.quebecteh.modules.inventary.picklist.model.dto.TotalPickListByStatus;

public interface PickListService extends MultiTenancyService<PickList, Long> {
	
	List<TotalPickListByStatus> getTotalByStatus(String tenantId);

}

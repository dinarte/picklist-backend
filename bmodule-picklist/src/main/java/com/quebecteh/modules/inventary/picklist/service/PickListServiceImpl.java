package com.quebecteh.modules.inventary.picklist.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quebecteh.commons.multitenancy.AbstractMultiTenancyCrudService;
import com.quebecteh.modules.inventary.picklist.model.domain.PickList;
import com.quebecteh.modules.inventary.picklist.model.dto.TotalPickListByStatus;
import com.quebecteh.modules.inventary.picklist.repository.PickListRepository;

@Service
public class PickListServiceImpl extends AbstractMultiTenancyCrudService<PickList, Long> 
                                 implements PickListService {


	@Autowired
	PickListRepository picklistRepository;
	
	public List<TotalPickListByStatus> getTotalByStatus(String tenantId) {
		return picklistRepository.getTotalByStatusByTentantId(tenantId);
	}

}

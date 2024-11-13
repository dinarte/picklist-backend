package com.quebecteh.modules.inventary.picklist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.quebecteh.commons.multitenancy.MultiTenancyCrudRepository;
import com.quebecteh.modules.inventary.picklist.model.domain.PickList;
import com.quebecteh.modules.inventary.picklist.model.dto.TotalPickListByStatus;

public interface PickListRepository extends MultiTenancyCrudRepository<PickList, Long>{
	
	@Query("SELECT p.status as status, COUNT(p) as total FROM PickList p WHERE tenantId = :tenantId  GROUP BY p.status")
	public List<TotalPickListByStatus> getTotalByStatusByTentantId(@Param("tenantId") String tenantId);
	
}

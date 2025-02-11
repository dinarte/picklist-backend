package com.quebecteh.modules.commons.connector.service;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.quebecteh.commons.multitenancy.AbstractMultiTenancyCrudService;
import com.quebecteh.modules.commons.connector.model.domain.ZohoOrganization;

import jakarta.transaction.Transactional;

@Service
public class ZohoOrganizationServiceImpl extends AbstractMultiTenancyCrudService<ZohoOrganization, Long> 
										 implements ZohoOrganizationService  {
	

	@Transactional
	public Iterable<ZohoOrganization> saveOrUpdateAllByOrganizationId(Iterable<ZohoOrganization> organizations, String tenantId) {
		organizations.forEach(obj -> {
			var criteria = "organizationId = '"+obj.getOrganizationId()+"' and tenantId='"+tenantId+"' and connection.id = " + obj.getConnection().getId();
			if (countWhere(criteria) > 0) {
				obj = Optional.of(findFristWhere(criteria, null)).orElse(obj);
			}
			this.saveOrUpdate(obj);
		});
		return organizations;
    }
	
}

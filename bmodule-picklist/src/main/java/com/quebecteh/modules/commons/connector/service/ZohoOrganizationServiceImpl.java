package com.quebecteh.modules.commons.connector.service;
import org.springframework.stereotype.Service;

import com.quebecteh.commons.multitenancy.AbstractMultiTenancyCrudService;
import com.quebecteh.modules.commons.connector.model.domain.ZohoOrganization;

@Service
public class ZohoOrganizationServiceImpl extends AbstractMultiTenancyCrudService<ZohoOrganization, Long> 
										 implements ZohoOrganizationService  {
	

	
	public Iterable<ZohoOrganization> saveOrUpdateAllByOrganizationId(Iterable<ZohoOrganization> organizations) {
		organizations.forEach(obj -> {
			if (countBy("organizationId", obj.getOrganizationId()) > 0) {
				obj = findOneBy("organizationId", obj.getOrganizationId());
			}
			this.saveOrUpdate(obj);
		});
		return organizations;
    }
	
}

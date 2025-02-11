package com.quebecteh.modules.commons.connector.service;

import com.quebecteh.commons.multitenancy.MultiTenancyService;
import com.quebecteh.modules.commons.connector.model.domain.ZohoOrganization;

public interface ZohoOrganizationService extends MultiTenancyService<ZohoOrganization, Long> {

	public Iterable<ZohoOrganization> saveOrUpdateAllByOrganizationId(Iterable<ZohoOrganization> organizations, String tenantId);

}
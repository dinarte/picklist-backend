package com.quebecteh.modules.commons.connector.service;

import com.quebecteh.commons.multitenancy.MultiTenancyService;
import com.quebecteh.modules.commons.connector.model.domain.ZohoConnection;

public interface ZohoConnectionService extends MultiTenancyService<ZohoConnection, Long> {

	 /**
     * Renews the authentication token if it is expired or will expire soon.
     *
     * @param tenantId The tenant ID whose connection token needs renewal.
     * @param conn The ZohoConnection object containing current connection details.
     */
	public void renewConnectionIsExipered(ZohoConnection conn);

}
package com.quebecteh.modules.inventary.picklist.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quebecteh.modules.commons.clients.api.zoho.model.ZohoResponse;
import com.quebecteh.modules.commons.clients.api.zoho.service.ZohoApiClientSalesOrderService;
import com.quebecteh.modules.commons.connector.service.ZohoConnectionService;
import com.quebecteh.modules.inventary.picklist.model.domain.PickListUserAuth;

@Service
public class ZohoInventarySalesOrderService {

	@Autowired	ZohoConnectionService zohoConnectionService;
	@Autowired	ZohoApiClientSalesOrderService salesOrderService;
	@Autowired PickListUserAuth auth;
	
	public ZohoResponse setSalesOrderAsReadyToShipping(String organizationId, String salesOrdersId) {
		var conn = auth.getConn();
		zohoConnectionService.renewConnectionIfExipered(conn);
		return salesOrderService.setSubStatus(organizationId, salesOrdersId, conn.getAccesToken(), "cs_readyto");
	}
	
}

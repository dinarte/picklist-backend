package com.quebecteh.modules.migrators.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.quebecteh.modules.commons.clients.api.zoho.connector.ZohoConnectorProperties;
import com.quebecteh.modules.migrators.repository.MigrationLogRepository;
import com.quebecteh.modules.migrators.service.JDBCSourceConnectionService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class PurchaseOrderBillsMigrationController extends AbstractInventoryMigtationDefaultColntroller {

	
	public static String INVENTORY_ASSET_ACCOUNT = "5304025000000034001";
	

	public PurchaseOrderBillsMigrationController(ZohoConnectorProperties connProperties,
			JDBCSourceConnectionService connectionService, MigrationLogRepository migrationLogRepository) {
		super(connProperties, connectionService, migrationLogRepository);
		
	}
	
	protected MigrationConfiguration getCofiguration() {
		
		return MigrationConfiguration.builder()
			.tenantId("bave-" + zohoOrganizationId)	
			.sourceAppName("conciat")
			.sourceEntity("purchaseorders")
			.sourceFieldId("id")
			.destinationApp("zoho-inventory")
			.destinationEntity("bill")
			.destinationFieldId("bill_id")
			.destinationResource("bills")
			.build();
	}
	
	
	protected String getSql(List<String> migratedIds) {
		
		String sqlMigratedsIds = "";
		if (migratedIds != null && migratedIds.size() > 0) {
			sqlMigratedsIds = migratedIds.stream().collect(Collectors.joining(","));
			sqlMigratedsIds = "AND po.id not in("+sqlMigratedsIds+")\r\n";
		}
		
		return 
				" SELECT \r\n"
				+ "    po.id, \r\n"
				+ "    po.po_number, \r\n"
				+ "    DATE_FORMAT(po.date_creation, '%Y-%m-%d') as date_creation, \r\n"
				+ "    DATE_FORMAT(po.date_estimated_delivery, '%Y-%m-%d') as date_estimated_delivery, \r\n"
				+ "    DATE_FORMAT(po.date_payment_due, '%Y-%m-%d') as date_payment_due, \r\n"
				+ "    po.id_vendor, \r\n"
				+ "    po.notes,\r\n"
				+ "    po.value_total,\r\n"
				+ "    (po.value_orther_cost + po.value_shipping_cost)  as total_adjustments,\r\n"
				+ "    SUM(pod.value_total) as total_items,\r\n"
				+ "    JSON_ARRAYAGG(\r\n"
				+ "        JSON_OBJECT(\r\n"
				+ "            'id', pod.id,\r\n"
				+ "            'id_purchase_order', pod.id_purchase_order,\r\n"
				+ "            'id_product', p.id,\r\n"
				+ "            'product_name', p.name,\r\n"
				+ "            'product_description', p.description,\r\n"
				+ "            'value_unit', pod.value_unit,\r\n"
				+ "            'quantity', pod.quantity,\r\n"
				+ "            'quantity_to_inventory', pod.quantity_to_inventory,\r\n"
				+ "            'value_total', pod.value_total,\r\n"
				+ "            'product_cost', pod.product_cost\r\n"
				+ "        )\r\n"
				+ "    ) AS items\r\n"
				+ "FROM \r\n"
				+ "    purchase_order po\r\n"
				+ "JOIN \r\n"
				+ "    purchase_order_detail pod ON pod.id_purchase_order = po.id \r\n"
				+ "JOIN \r\n"
				+ "    products p ON p.id = pod.id_product \r\n"
				+ "WHERE  pod.is_active = true\r\n"
				+ "AND    po.is_active = true\r\n"
				+ "AND    p.is_active is true\r\n"
				+ "AND    po.id_purchase_order_status <> 6\r\n"
				//+ "AND po.id = 1032 \r\n"
				+ sqlMigratedsIds
				+ "\r\nGROUP BY \r\n"
				+ "    po.id \r\n" ;
				//+ "LIMIT 5 OFFSET 0";
	}
	

	protected Map<String, Object> getMappedJson(Map<String, Object> resultMap) {
		
		log.info("Executing mapping: {}", resultMap.get("id"));
		
		String zohoPurchaseorderId = getMigratedIdValue("purchaseorders", resultMap.get("id"), "purchaseorder");
		String zohoVendorId = getMigratedIdValue("vendor", resultMap.get("id_vendor"), "contact");
		
		 Map<String, Object> valuesMapped = Map.ofEntries(
		            Map.entry("purchaseorder_id", zohoPurchaseorderId),
		            Map.entry("vendor_id", zohoVendorId),
		            Map.entry("bill_number", "CBL-" + resultMap.get("po_number")),
		            Map.entry("date", resultMap.get("date_creation")),
		            Map.entry("due_date", resultMap.get("date_payment_due")), 
		            Map.entry("reference_number", "CONCIAT-" +  resultMap.get("id").toString()),
		            Map.entry("currency_id", "5304025000000000097"),
		            Map.entry("is_item_level_tax_calc", true),
		            Map.entry("notes", resultMap.get("notes")),				
		            Map.entry("sub_total", resultMap.get("total_items")),
		    		Map.entry("adjustment", resultMap.get("total_adjustments")),
		    		Map.entry("adjustment_description", "Others Costs"),
		    		Map.entry("total", resultMap.get("value_total")),
		            Map.entry("line_items", getItemsMapped(resultMap.get("id")))
		

		        );
		
		return valuesMapped;
	}


	protected List<Map<String, Object>> getItemsMapped(Object sourceId) {
		
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> items  = (List<Map<String, Object>>) getSuccessMigrationLog("purchaseorders", sourceId, "purchaseorder")
																				.getDestinationData().getData().get("line_items");
		
		//List<Map<String, Object>> items = jsonToMap(itemsJsonStr);
		
		var itemsMapped = items.stream().map(item -> {
			
			return Map.of(
				    "purchaseorder_item_id", item.get("line_item_id"),
				    "item_id", item.get("item_id"),
				    "rate", item.get("rate"),
				    "quantity", item.get("quantity"),
				    //"discount", 0.00,
				    "item_total", item.get("item_total"),
				    "account_id", INVENTORY_ASSET_ACCOUNT
				    );
				
			}).collect(Collectors.toList());
		
		return itemsMapped;
	}


}

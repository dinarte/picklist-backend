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
public class PurchaseOrderMigrationController extends AbstractInventoryMigtationDefaultColntroller {
	
	public PurchaseOrderMigrationController(ZohoConnectorProperties connProperties,
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
			.destinationEntity("purchaseorder")
			.destinationFieldId("purchaseorder_id")
			.destinationResource("purchaseorders")
			.build();
	}
	
	
	protected String getSql(List<String> migratedIds) {
		
		String sqlMigratedsIds = "";
		if (migratedIds != null && migratedIds.size() > 0) {
			sqlMigratedsIds = migratedIds.stream().collect(Collectors.joining(","));
			sqlMigratedsIds = "AND po.id not in("+sqlMigratedsIds+")\r\n";
		}
		
		return 
				" SELECT\r\n"
				+ "    po.id, po.po_number, \r\n"
				+ "    DATE_FORMAT(po.date_creation, '%Y-%m-%d') as date_creation, \r\n"
				+ "    DATE_FORMAT(po.date_estimated_delivery, '%Y-%m-%d') as date_estimated_delivery, \r\n"
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
				+ "WHERE \r\n"
				+ "    pod.is_active = true\r\n"
				+ "    and po.id_purchase_order_status <> 6\r\n"
				+ "    and p.is_active is true\r\n"
				//+ "AND po.id = 1032 \r\n"
				+ sqlMigratedsIds
				+ "\r\nGROUP BY \r\n"
				+ "    po.id \r\n";
				//+ "LIMIT 5 OFFSET 0"; 
	}
	
	
	protected Map<String, Object> getMappedJson(Map<String, Object> resultMap) {
		
		log.info("Executing mapping: {}", resultMap.get("id"));
		 
		Map<String, Object> valuesMapped = Map.ofEntries(
		Map.entry("date", resultMap.get("date_creation")),
		Map.entry("expected_delivery_date", resultMap.get("date_estimated_delivery")), 
		Map.entry("purchaseorder_number", "CPO-" + resultMap.get("po_number")),
		Map.entry("reference_number", "CONCIAT-" +  resultMap.get("id").toString() ),
		Map.entry("vendor_id", getMigratedIdValue("vendor", resultMap.get("id_vendor"), "contact")),
		Map.entry("currency_id", "5304025000000000097"),
		Map.entry("currency_code", "USD"),
		Map.entry("currency_symbol", "$"),
		Map.entry("line_items", getItemsMapped(resultMap.get("items").toString())),
		Map.entry("discount_amount", 0.00),
		Map.entry("discount", 0.00),
		Map.entry("discount_applied_on_amount", 0.00),
		Map.entry("is_discount_before_tax", true),
		Map.entry("discount_account_id", ""),
		Map.entry("discount_account_name", ""),
		Map.entry("discount_type", "entity_level"),
		Map.entry("sub_total", resultMap.get("total_items")),
		Map.entry("adjustment", resultMap.get("total_adjustments")),
		Map.entry("adjustment_description", "Others Costs"),
		Map.entry("total", resultMap.get("value_total")),
		Map.entry("price_precision", 2),
		Map.entry("notes", ""),
		Map.entry("payment_terms", 0),
		Map.entry("attention", "Bave USA LLC")
		);
		
		return valuesMapped;
	}
	
	private List<Map<String, Object>> getItemsMapped(String itemsJsonStr) {
		
		
		List<Map<String, Object>> items = jsonToMap(itemsJsonStr);
		
		var itemsMapped = items.stream().map(item -> {
			return Map.of(
				    "item_id", getMigratedIdValue("product", item.get("id_product"), "item"),
				    "rate", item.get("value_unit"),
				    "quantity", item.get("quantity"),
				    //"discount", 0.00,
				    "item_total", item.get("value_total")
				    );
				
			}).collect(Collectors.toList());
		
		return itemsMapped;
	}

}

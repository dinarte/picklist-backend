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
public class ProvisionsToBillsMigrationController extends AbstractInventoryMigtationDefaultColntroller {

	
	//public static String INVENTORY_ASSET_ACCOUNT = "5304025000000034001";
	
	public static String COST_OF_GOODS_ACCOUNT = "5304025000001691131";
	
	/*
	 * public static Map<Object, String> LANDED_COST_ITEMS = Map.of(
	 * 
	 * 6, "5304025000001677009", //Freight on Sale (Florida) 1,
	 * "5304025000001677031", //Broker 12, "5304025000001677064", //Federal Tax 22,
	 * "5304025000001677086", //Freight on Purchase 34, "5304025000001677108",
	 * //State Tax 19, "5304025000001677141", //Building Maintenance 5,
	 * "5304025000001677163" //Bank Fee
	 * 
	 * );
	 */
	
	public ProvisionsToBillsMigrationController(ZohoConnectorProperties connProperties,
			JDBCSourceConnectionService connectionService, MigrationLogRepository migrationLogRepository) {
		super(connProperties, connectionService, migrationLogRepository);
		
	}
	
	protected MigrationConfiguration getCofiguration() {
		
		return MigrationConfiguration.builder()
			.tenantId("bave-" + zohoOrganizationId)	
			.sourceAppName("conciat")
			.sourceEntity("purchase_order_provision")
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
			sqlMigratedsIds = "AND pop.id not in("+sqlMigratedsIds+")\r\n";
		}
		
		return 
				"SELECT\r\n"
				+ 	"	pop.id, \r\n"
				+ 	"	DATE_FORMAT(pop.created_at, '%Y-%m-%d') AS date_creation,\r\n"
				+ 	"	pop.id_purchase_order,\r\n"
				+ 	"	pop.id_vendor,\r\n"
				+ 	"	pop.total_value,\r\n"
				+ 	"	pop.id_expense_category\r\n"
				+ "FROM \r\n "
				+ "purchase_order_provision pop\r\n"
				+ "JOIN \r\n "
				+ "purchase_order po ON po.id = pop.id_purchase_order\r\n" 
				+ "JOIN \r\n"
				+ "purchase_order_status pos ON pos.id = po.id_purchase_order_status\r\n" 
				+ "WHERE po.id_purchase_order_status <> 6\r\n" 
				+ sqlMigratedsIds;
				//+ " LIMIT 5 OFFSET 0";
	} 
	

	protected Map<String, Object> getMappedJson(Map<String, Object> resultMap) {
		
		log.info("Executing mapping: {}", resultMap.get("id"));
		
		String zohoPurchaseorderId = getMigratedIdValue("purchaseorders", resultMap.get("id_purchase_order"), "purchaseorder");
		String zohoVendorId = getMigratedIdValue("vendor", resultMap.get("id_vendor"), "contact");
		
		 Map<String, Object> valuesMapped = Map.ofEntries(
		            Map.entry("purchaseorder_id", zohoPurchaseorderId),
		            Map.entry("vendor_id", zohoVendorId),
		            Map.entry("bill_number", "PROV-" + resultMap.get("id")),
		            Map.entry("date", resultMap.get("date_creation")),
		            //Map.entry("due_date", resultMap.get("date_payment_due")), 
		            Map.entry("reference_number", "CONC.PROV_" +  resultMap.get("id").toString()),
		            Map.entry("currency_id", "5304025000000000097"),
		            //Map.entry("exchange_rate", 1),
		            //Map.entry("is_item_level_tax_calc", true),
		            //Map.entry("notes", resultMap.get("date_payment_due")),
		            //Map.entry("terms", ""),
		            //Map.entry("is_inclusive_tax", true),
					
		            Map.entry("line_items", List.of(	
		            		Map.of(
		        				    //"purchaseorder_item_id", zohoPurchaseorderId,
		        				    "item_id", getMigratedIdValue("expenses_category", resultMap.get("id_expense_category"), "item"),
		        				    "rate", resultMap.get("total_value"),
		        				    "quantity", 1,
		        				    "item_total", resultMap.get("total_value"),
		        				    "account_id", COST_OF_GOODS_ACCOUNT,
		        				    "is_landedcost", true
		        				  )
		            )
		       
		            //Map.entry("gst_treatment", "business_gst"),
		            //Map.entry("tax_treatment", "vat_registered"),
		            //Map.entry("gst_no", "22AAAAA0000A1Z5"),
		            //Map.entry("source_of_supply", "AP"),
		            //Map.entry("destination_of_supply", "TN")
		        ));
		
		return valuesMapped;
	}





}

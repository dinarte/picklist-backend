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
public class ExpensesToBillsMigrationController extends AbstractBooksMigtationDefaultColntroller {

	
	public static String COST_OF_GOODS_ACCOUNT = "5304025000001691131";
	
	public ExpensesToBillsMigrationController(ZohoConnectorProperties connProperties,
			JDBCSourceConnectionService connectionService, MigrationLogRepository migrationLogRepository) {
		super(connProperties, connectionService, migrationLogRepository);
		
	}
	
	protected MigrationConfiguration getCofiguration() {
		
		return MigrationConfiguration.builder()
			.tenantId("bave-" + zohoOrganizationId)	
			.sourceAppName("conciat")
			.sourceEntity("expenses")
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
			sqlMigratedsIds = "AND e.id not in("+sqlMigratedsIds+")\r\n";
		}
		
		return 
				"select  DISTINCT\r\n"
				+ "		e.id, \r\n"
				+ "		DATE_FORMAT(e.date_creation , '%Y-%m-%d') AS date_creation,\r\n"
				+ "		DATE_FORMAT(e.date_payment_due , '%Y-%m-%d') AS date_payment_due,\r\n"
				+ "		e.id_po,\r\n"
				+ "		e.id_vendor,\r\n"
				+ "		e.total,\r\n"
				+ "		e.name ,\r\n"
				+ "		e.id_expenses_category \r\n"
				+ "FROM  \r\n"
				+ "expenses e\r\n"
				+ "JOIN  \r\n"
				+ "expenses_category ec ON ec.id = e.id_expenses_category \r\n"
				+ "JOIN  \r\n"
				+ "purchase_order po ON po.id = e.id_po \r\n"
				+ "join purchase_order_detail pod on pod.id_purchase_order = po.id \r\n"
				+ "join products p on p.id  = pod.id_product \r\n"
				+ "JOIN \r\n"
				+ "purchase_order_status pos ON pos.id = po.id_purchase_order_status \r\n"
				+ "WHERE po.id_purchase_order_status <> 6 \r\n"
				+ "and po.is_active is true \r\n"
				+ "and pod.is_active is true\r\n"
				+ "and p.is_active is true \r\n" 
				+ sqlMigratedsIds;
				//+ " LIMIT 5 OFFSET 0";
	} 
	
	protected Map<String, Object> getMappedJson(Map<String, Object> resultMap) {
		
		log.info("Executing mapping: {}", resultMap.get("id"));
		
		//MigrationLog zohoPurchaseorderLog = getSuccessMigrationLog("purchaseorders", resultMap.get("id_po"), "purchaseorder");
		//String zohoPurchaseorderId = zohoPurchaseorderLog.getDestinationData().getData().get("purchaseorder_id").toString();
		//String zohoPurchaseorderNumber = zohoPurchaseorderLog.getDestinationData().getData().get("purchaseorder_number").toString();
		//String zohoPurchaseorderId = getMigratedIdValue("purchaseorders", resultMap.get("id_po"), "purchaseorder");
		String zohoVendorId = getMigratedIdValue("vendor", resultMap.get("id_vendor"), "contact");
		
		 Map<String, Object> valuesMapped = Map.ofEntries(
		            //Map.entry("purchaseorder_id", zohoPurchaseorderId),
		            Map.entry("vendor_id", zohoVendorId),
		            Map.entry("bill_number", "CLD-" + resultMap.get("id")),
		            Map.entry("date", resultMap.get("date_creation")),
		            //Map.entry("due_date", resultMap.get("date_payment_due")), 
		            Map.entry("reference_number", "CPOS:" + resultMap.get("id_po")),
		            Map.entry("currency_id", "5304025000000000097"),
		            Map.entry("notes", resultMap.get("name")),
	
					
		            Map.entry("line_items", List.of(	
		            		Map.of(
		        				    //"purchaseorder_item_id", zohoPurchaseorderId,
		        				    "item_id", getMigratedIdValue("expenses_category", resultMap.get("id_expenses_category"), "item"),
		        				    "rate", resultMap.get("total"),
		        				    "quantity", 1,
		        				    "item_total", resultMap.get("total"),
		        				    "account_id", COST_OF_GOODS_ACCOUNT,
		        				    "is_landedcost", true
		        				  )
		            )
		       
		        ));
		
		return valuesMapped;
	}





}

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
public class ExpensesCategoryToItemsMigrationController extends AbstractInventoryMigtationDefaultColntroller {

	public ExpensesCategoryToItemsMigrationController(ZohoConnectorProperties connProperties,
			JDBCSourceConnectionService connectionService, MigrationLogRepository migrationLogRepository) {
		super(connProperties, connectionService, migrationLogRepository);
		// TODO Auto-generated constructor stub
	}
	
	public MigrationConfiguration getCofiguration() {
		
		return MigrationConfiguration.builder()
			.tenantId("bave-" + zohoOrganizationId)	
			.sourceAppName("conciat")
			.sourceEntity("expenses_category")
			.sourceFieldId("id")
			.destinationApp("zoho-inventory")
			.destinationEntity("item")
			.destinationFieldId("item_id")
			.destinationResource("items")
			.build();
	}
	
	
	public String getSql(List<String> migratedIds) {
		
		String sqlMigratedsIds = "";
		if (migratedIds != null && migratedIds.size() > 0) {
			sqlMigratedsIds = migratedIds.stream().collect(Collectors.joining(","));
			sqlMigratedsIds = "and ec.id not in("+sqlMigratedsIds+")\r\n";
		}
		
		return 
				" select distinct ec.id, ec.name\r\n"
				+ "from expenses e \r\n"
				+ "join expenses_category ec on ec.id = e.id_expenses_category\r\n"
				+ "join purchase_order po on po.id = e.id_po\r\n"
				+ "where po.id_purchase_order_status <> 6\r\n"
				+ sqlMigratedsIds
				+ "\r\norder by id";
				//+ " and p.id=1358";
				//+ "LIMIT 5 OFFSET 0";
	}
	

	public Map<String, Object> getMappedJson(Map<String, Object> resultMap) {
		
		 log.info("Executing mapping: {}", resultMap.get("id"));
		 
		 Map<String, Object> valuesMapped = Map.ofEntries(
		            Map.entry("unit", "Unit"),
		            Map.entry("item_type", "purchases"),
		            Map.entry("product_type", "service"),
		            Map.entry("description", resultMap.get("name")),
		            //Map.entry("purchase_account_id", 5755892000000034003L),  
		            //Map.entry("inventory_account_id", 5755892000000034001L),
		            
		            Map.entry("purchase_account_id", 5304025000001691131L),  
		            //Map.entry("inventory_account_id", 5304025000000034001L),
		            
		            Map.entry("name", resultMap.get("name")),
		            //Map.entry("rate", ""),
		            Map.entry("purchase_rate", 0),
		            Map.entry("reorder_level", 10),
		            Map.entry("category_id", 5304025000001664911L),
		            //Map.entry("initial_stock", resultMap.get("quantity")),
		            //Map.entry("initial_stock_rate", resultMap.get("stock_rate")),
		            //Map.entry("initial_stock", 0),
		            //Map.entry("initial_stock_rate", 0),
		            //Map.entry("track_batch_number", true),
		            //Map.entry("vendor_id", 4815000000044080L),
		            //Map.entry("vendor_name", "Molly"),
		            //Map.entry("sku", "SK123"),
		            Map.entry("upc", ""),
		            Map.entry("purchase_description", resultMap.get("name"))
		            /*Map.entry("package_details", 
		                Map.of(
		                		"length", resultMap.get("product_d"),
		        	            "width", resultMap.get("product_w"),
		        	            "height", resultMap.get("product_h"),
		        	            "weight", resultMap.get("weight"),
		        	            "weight_unit", "lb",
		        	            "dimension_unit", "in"
			                )
			        )*/
		            /*Map.entry("custom_fields", new Object[] {
		                Map.of( 
		                	 
		                		"customfield_id", CUSTOM_FIELD_ALCOHOLIC_ID,
		                		"value", alcholicDrinkinsIds.contains( (Integer) resultMap.get("id_type") )
		                ),
		                Map.of(
		                		
		                		"customfield_id", CUSTOM_FIELD_VOLUME_ID  ,
		                		"value", volume
		                ),
		                Map.of(
		                		
		                		"customfield_id", CUSTOM_BOTTLES_ID  ,
		                		"value", bottles
		                ),
		                
		            })*/
		        );
		
		return valuesMapped;
	}
	 
}

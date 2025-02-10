package com.quebecteh.modules.migrators.controller;

import java.math.BigDecimal;
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
public class ItemMigrationController extends AbstractInventoryMigtationDefaultColntroller {

	
	
	public ItemMigrationController(ZohoConnectorProperties connProperties,
			JDBCSourceConnectionService connectionService, MigrationLogRepository migrationLogRepository) {
		super(connProperties, connectionService, migrationLogRepository);

	}
	
	private final static List<Integer> alcholicDrinkinsIds = List.of(15,16,17);
	
	private final static String CUSTOM_FIELD_ALCOHOLIC_ID = "5304025000000168001";
	
	private final static String CUSTOM_FIELD_VOLUME_ID = "5304025000000170436";
	
	private final static String CUSTOM_BOTTLES_ID = "5304025000001738528";
	
	private final static Double LITRE_VS_GALOON = 3.785411784;
	
	private final static Map<Integer, Integer> PRODUCTS_BOTTLES_MAP = Map.of(
				1002, 24, //Beer Brahma Chopp 355ml 4x6-Pack	
				1003, 12, //Beer Brahma Duplo Malte 12x350ml 
				1004, 24, //Beer Brahma Malzbier 355ml 4x6-Pack	
				1033, 24  //Beer Bohemia Puro Malte 330ml 4x6-Pack
			); 
	
	
	
   private final static Map<Object, String> CATEGORY_MAP = Map.ofEntries(
		   Map.entry(14, "5304025000001738546"), // Dry Food
		   Map.entry(15, "5304025000001738550"), // Beverage - Liquor
		   Map.entry(16, "5304025000001738554"), // Beverage - Wine
		   Map.entry(17, "5304025000001738558"), // Beverage - Beer
		   Map.entry( 18, "5304025000001738562"), // Freight
		   Map.entry( 19, "5304025000001738566"), // Service - Non-Inventory
		   Map.entry(20, "5304025000001738570"), // Soft Drink
		   Map.entry(21, "5304025000001738574"), // Marketing Merchandise
		   Map.entry(22, "5304025000001738578"), // Refrigerated
		   Map.entry(23, "5304025000001738582"), // Frozen
		   Map.entry(24, "5304025000001738586")  // Flowers
        );
	
	
	public MigrationConfiguration getCofiguration() {
		
		return MigrationConfiguration.builder()
			.tenantId("bave-" + zohoOrganizationId)	
			.sourceAppName("conciat")
			.sourceEntity("product")
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
			sqlMigratedsIds = "where p.id not in("+sqlMigratedsIds+")\r\n";
		}
		
		return 
				"  with tab_product as (\r\n"
				+ "	 select 	p.id, \r\n"
				+ "	 			pt.id as id_type, \r\n"
				+ "	 			m.name as unit, \r\n"
				+ "	 			p.description, \r\n"
				+ "	 			p.name, \r\n"
				+ "	 			p.sku, \r\n"
				+ "	 			p.id_product_type, \r\n"
				+ "	 			REPLACE(LEFT(TRIM(p.product_upc),13), 'n/a', '') as product_upc, \r\n"
				+ "	 			COALESCE(ppld.value_sales_price,0) as value_sales_price, \r\n"
				+ "	 			COALESCE(pod.value_unit, 0) as value_unit, \r\n"
				+ "	 			COALESCE(pi.quantity, 0) as quantity, \r\n"
				+ "	 			COALESCE(pod.value_unit, 0) as stock_rate,  \r\n"
				+ "	 			COALESCE(p.product_volume, 0) as product_volume, \r\n"
				+ "	 			p.product_w, p.product_d, p.product_h, p.weight, p.is_active\r\n"
				+ "	 from products p\r\n"
				+ "	 join product_type pt on pt.id = p.id_product_type\r\n"
				+ "	 join measurement m on m.id = p.id_measurement \r\n"
				+ "	 left join product_price_list_detail ppld on ppld.id_product = p.id and ppld.id_product_price_list = 1004\r\n"
				+ "	 left join purchase_order_detail pod on pod.id_product = p.id \r\n"
				+ "	 	 and  pod.id in (select max(pod2.id) from purchase_order_detail pod2 where pod2.id_product = p.id)\r\n"
				+ "	 left join product_inventory pi on pi.id_product = p.id\r\n"
				//+ "	 where p.is_active = true\r\n"
				+ " ),\r\n"
				+ " tab_receive as (\r\n"
				+ "	 select pod.id_product, concat(por.lot_number,' (',por.expiration_date,')') as lot_number, por.expiration_date, sum(por.quantity_receipt) as initial_stock\r\n"
				+ "	 from purchase_order_receipt por \r\n"
				+ "	 join purchase_order_detail pod on pod.id = por.id_purchase_order_detail \r\n"
				+ "	 where por.is_receipt is true \r\n"
				+ "	 and por.is_active is true\r\n"
				+ "	 and pod.is_active is true\r\n"
				+ "	 group by pod.id_product, concat(por.lot_number,' (',por.expiration_date,')'), por.expiration_date\r\n"
				+ "	 order by id_product \r\n"
				+ ")\r\n"
				+ "select p.*, \r\n"
				+ "sum(coalesce(initial_stock, 0)) as initial_stok,\r\n"
				+ "	JSON_ARRAYAGG(\r\n"
				+ "		JSON_OBJECT(\r\n"
				+ "			'lot_number', lot_number,\r\n"
				+ "			'expiration_date', expiration_date,\r\n"
				+ "			'initial_stock',  coalesce(initial_stock, 0)\r\n"
				+ "		)\r\n"
				+ "	) as batches\r\n"
				+ "from tab_product p\r\n"
				+ "left join tab_receive r on p.id = r.id_product\r\n" 
				+ sqlMigratedsIds + "\r\n"
				+ "group by p.id, p.id_type, p.unit, p.description, p.name, p.sku, p.id_product_type, \r\n"
				+ "		 p.product_upc, p.value_sales_price, p.value_unit, p.quantity, p.stock_rate,\r\n"
				+ "		 p.product_volume, p.product_w, p.product_d, p.product_h, p.weight\r\n";
				
				//+ "LIMIT 5 OFFSET 0";
	}
	

	public Map<String, Object> getMappedJson(Map<String, Object> resultMap) {
		
		 log.info("Executing mapping: {}", resultMap.get("id"));
		 
		 Double volume = ((BigDecimal) resultMap.get("product_volume")).doubleValue();
		 volume = volume.equals(0.0) ? 0.0 : volume / LITRE_VS_GALOON;
		 
		 Integer bottles =  PRODUCTS_BOTTLES_MAP.get( resultMap.get("id") );
		 bottles = bottles == null ? 0 : PRODUCTS_BOTTLES_MAP.get( resultMap.get("id") ); 
		 
		 Map<String, Object> valuesMapped = Map.ofEntries(
		            Map.entry("unit", "Unit"),
		            Map.entry("item_type", "inventory"),
		            Map.entry("product_type", "goods"),
		            Map.entry("description", resultMap.get("description")),
		            //Map.entry("purchase_account_id", 5755892000000034003L),  
		            //Map.entry("inventory_account_id", 5755892000000034001L),
		            
		            Map.entry("purchase_account_id", 5304025000000034003L),  
		            Map.entry("inventory_account_id", 5304025000000034001L),
		            
		            Map.entry("name", resultMap.get("name") + ((int) resultMap.get("is_active") == 1 ? "" : "(Inactive)")),
		            Map.entry("rate", resultMap.get("value_sales_price")),
		            Map.entry("purchase_rate", resultMap.get("value_unit")),
		            Map.entry("reorder_level", 10),
		            Map.entry("category_id", CATEGORY_MAP.get(resultMap.get("id_product_type"))),
		            //Map.entry("initial_stock", resultMap.get("quantity")),
		            //Map.entry("initial_stock_rate", resultMap.get("stock_rate")),
		            Map.entry("initial_stock", 0),
		            Map.entry("initial_stock_rate", 0),
		            Map.entry("track_batch_number", true),
		            Map.entry("upc", resultMap.get("product_upc")),
		            Map.entry("purchase_description", resultMap.get("description")),
		            Map.entry("package_details", 
		                Map.of(
		                		"length", resultMap.get("product_d"),
		        	            "width", resultMap.get("product_w"),
		        	            "height", resultMap.get("product_h"),
		        	            "weight", resultMap.get("weight"),
		        	            "weight_unit", "lb",
		        	            "dimension_unit", "in"
			                )
			        ),
		            Map.entry("custom_fields", new Object[] {
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
		                
		            })
		        );
		
		return valuesMapped;
	}


}

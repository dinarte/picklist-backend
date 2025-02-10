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
public class PriceListMigrationController extends AbstractInventoryMigtationDefaultColntroller {


	
	public PriceListMigrationController(ZohoConnectorProperties connProperties,
			JDBCSourceConnectionService connectionService, MigrationLogRepository migrationLogRepository) {
		super(connProperties, connectionService, migrationLogRepository);
		
	}


	protected MigrationConfiguration getCofiguration() {
		
		return MigrationConfiguration.builder()
			.tenantId("bave-" + zohoOrganizationId)	
			.sourceAppName("conciat")
			.sourceEntity("product_price_list")
			.sourceFieldId("id")
			.destinationApp("zoho-inventory")
			.destinationEntity("pricebook")
			.destinationFieldId("pricebook_id")
			.destinationResource("pricebooks")
			.build();
	}
	
	
	protected String getSql(List<String> migratedIds) {
		
		String sqlMigratedsIds = "";
		if (migratedIds != null && migratedIds.size() > 0) {
			sqlMigratedsIds = migratedIds.stream().collect(Collectors.joining(","));
			sqlMigratedsIds = "AND ppl.id not in("+sqlMigratedsIds+")\r\n";
		}
		
		return "select ppl.id, ppl.name,\r\n"
				+ "	JSON_ARRAYAGG(\r\n"
				+ "		JSON_OBJECT(\r\n"
				+ "			'id', ppld.id,\r\n"
				+ "			'id_product', ppld.id_product, \r\n"
				+ "			'value_sales_price', ppld.value_sales_price\r\n"
				+ "		)\r\n"
				+ "	) as items\r\n"
				+ "from product_price_list ppl \r\n"
				+ "join product_price_list_detail ppld on ppld.id_product_price_list = ppl.id\r\n"
				+ "join products p on p.id = ppld.id_product\r\n"
				+ "where exists (\r\n"
				+ " select so.id from  sales_order so where so.id_product_price_list = ppl.id and so.is_active is true\r\n"
				+ ")\r\n"
				+ "and ppld.is_active is true\r\n"
				+ "and p.is_active is true\r\n"
				+ sqlMigratedsIds
				+ "\r\ngroup by ppl.id";
	}
	
	
	protected Map<String, Object> getMappedJson(Map<String, Object> resultMap) {
		
		log.info("Executing mapping: {}", resultMap.get("id"));
		 
		Map<String, Object> valuesMapped = Map.ofEntries(
		Map.entry("name", resultMap.get("name")),
		Map.entry("description", resultMap.get("name") + " CONCIAT(#" +resultMap.get("id")+")"),
		Map.entry("currency_id", "5304025000000000097"),
		Map.entry("pricing_scheme", "unit"),
		Map.entry("pricebook_type", "per_item"),
		Map.entry("is_increase", true),
		Map.entry("rounding_type", "no_rounding"),
		Map.entry("sales_or_purchase_type", "sales"),
		Map.entry("pricebook_items", getItemsMapped(resultMap.get("items").toString()))
		
		);
		
		return valuesMapped;
	}
	
	private List<Map<String, Object>> getItemsMapped(String itemsJsonStr) {
		
		
		List<Map<String, Object>> items = jsonToMap(itemsJsonStr);
		
		var itemsMapped = items.stream().map(item -> {
			return Map.of(
					"item_id", getMigratedIdValue("product", item.get("id_product"), "item"),
				    "pricebook_rate", item.get("value_sales_price")
				    );
				
			}).collect(Collectors.toList());
		
		return itemsMapped;
	}

}

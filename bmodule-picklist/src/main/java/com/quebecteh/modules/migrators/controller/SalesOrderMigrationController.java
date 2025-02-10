package com.quebecteh.modules.migrators.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
public class SalesOrderMigrationController extends AbstractInventoryMigtationDefaultColntroller {

	private static final List<String> COALHO_CHEESE_PRODUCT_ID = List.of("1052","1116");
			
	public SalesOrderMigrationController(ZohoConnectorProperties connProperties,
			JDBCSourceConnectionService connectionService, MigrationLogRepository migrationLogRepository) {
		super(connProperties, connectionService, migrationLogRepository);
		
	}


	protected MigrationConfiguration getCofiguration() {
		
		return MigrationConfiguration.builder()
			.tenantId("bave-" + zohoOrganizationId)	
			.sourceAppName("conciat")
			.sourceEntity("sales_order")
			.sourceFieldId("id")
			.destinationApp("zoho-inventory")
			.destinationEntity("salesorder")
			.destinationFieldId("salesorder_id")
			.destinationResource("salesorders")
			.build();
	}
	
	
	protected String getSql(List<String> migratedIds) {
		
		String sqlMigratedsIds = "";
		if (migratedIds != null && migratedIds.size() > 0) {
			sqlMigratedsIds = migratedIds.stream().collect(Collectors.joining(","));
			sqlMigratedsIds = "AND so.id not in("+sqlMigratedsIds+")\r\n";
		}
		
		return "with coalho_cheese as (\r\n"
				+ "				select 		i.id, so.id as id_sales_order, p.id as id_product, d.value_unit, sum(d.total_weight) as weight,\r\n"
				+ "							(round(sum(d.total_weight) * d.value_unit,2) - sum(d.value_total) between -5 and 5) as is_coalho_cheese_valid,\r\n"
				+ "							sum(d.value_total) - round(sum(d.total_weight) * d.value_unit,2) as coalho_cheese_adjustment\r\n"
				+ "				from invoice i \r\n"
				+ "				join invoice_detail d on d.id_invoice = i.id\r\n"
				+ "				join products p on p.id = d.id_product\r\n"
				+ "				join sales_order so on so.id = i.id_sales_order\r\n"
				+ "				where d.value_unit <=10\r\n"
				+ "				and p.id in (1052, 1116)\r\n"
				+ "				and  round(d.total_weight * d.value_unit,2) - d.value_total between -5 and 5\r\n"
				+ "				and i.value_total_invoice > 0\r\n"
				+ "				and i.id_invoice_status <> 4\r\n"
				+ "				and so.id_sales_order_status <> 5\r\n"
				+ "				group by i.id, so.id, p.id, d.value_unit\r\n"
				+ "				order by i.id\r\n"
				+ ")\r\n"
				+" select so.id, so.id_customer, so.po_number, \r\n"
				+ " DATE_FORMAT(so.date_creation, '%Y-%m-%d') as date_creation, \r\n"
				+ " DATE_FORMAT(coalesce(so.date_submission, so.date_creation), '%Y-%m-%d') as date_submission, \r\n"
				+ " DATE_FORMAT(coalesce(so.date_estimated_delivery, date_submission + INTERVAL 30 DAY),'%Y-%m-%d') as date_estimated_delivery,\r\n"
				+ " 		so.notes, id_product_price_list, id_sales_order_status,\r\n"
				+ " 		so.id_system_persona_sales_rep, sp.name,\r\n"
				+ " 		JSON_ARRAYAGG(\r\n"
				+ " 				JSON_OBJECT(\r\n"
				+ " 					'id', sod.id,\r\n"
				+ " 					'id_product', sod.id_product,\r\n"
				+ " 					'value_unit', sod.value_unit,\r\n"
				+ " 					'quantity', sod.quantity,\r\n"
				+ " 					'value_total', sod.value_total, \r\n"
				+ " 					'quantity_coalho', (select weight from coalho_cheese cc where cc.id_sales_order = sod.id_sales_order and cc.id_product = sod.id_product), \r\n"
				+ " 					'value_unit_coalho', (select value_unit from coalho_cheese cc where cc.id_sales_order = sod.id_sales_order and cc.id_product = sod.id_product), \r\n"
				+ "						'is_coalho_cheese_valid', (select is_coalho_cheese_valid from coalho_cheese cc where cc.id_sales_order = sod.id_sales_order and cc.id_product = sod.id_product), \r\n"
				+ "						'coalho_cheese_adjustment', (select coalho_cheese_adjustment from coalho_cheese cc where cc.id_sales_order = sod.id_sales_order and cc.id_product = sod.id_product) \r\n"
				+ " 				)\r\n"
				+ " 		) as items\r\n"
				+ " from sales_order so \r\n"
				+ " left join system_persona sp on sp.id = so.id_system_persona_sales_rep \r\n"
				+ " join sales_order_detail sod on sod.id_sales_order = so.id \r\n"
				+ " join products p on p.id = sod.id_product \r\n"
				+ " where so.id_sales_order_status <> 5 #canceled\r\n"
				+ " and so.is_active is true\r\n"
				+ " and so.id_customer is not null\r\n"
				+ " and sod.quantity > 0"
				//+ " and so.id = 3314\r\n"
				+ " and sod.is_active is true\r\n"
				+ sqlMigratedsIds
				+ "\r\n group by so.id";
				//+ "\r\n LIMIT 5 OFFSET 0";
	}
	
	
	protected Map<String, Object> getMappedJson(Map<String, Object> resultMap) {
		
		log.info("Executing mapping: {}", resultMap.get("id"));
		
		String custumerId;
		try{
			custumerId = getMigratedIdValue("customer", resultMap.get("id_customer"), "contact");
		} catch (Exception e) {
			throw new RuntimeException("Zoho CustumerId no found for SsalesOrder: " + resultMap.get("id"), e);
		}
		
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateSubmission = LocalDate.parse(resultMap.get("date_submission").toString(), formatter);
        LocalDate dateDelivery = LocalDate.parse(resultMap.get("date_estimated_delivery").toString(), formatter);	
        String date = resultMap.get("date_submission").toString();
        String shipmentDate = resultMap.get("date_submission").toString();
        if (dateSubmission.isAfter(dateDelivery)) {
			shipmentDate = date;
		}
		
		 Map<String, Object> valuesMapped = Map.ofEntries(
				 Map.entry("customer_id", custumerId),
				 Map.entry("salesorder_number", "CSO-" + resultMap.get("id")),
				 Map.entry("date", date),
				 Map.entry("shipment_date", shipmentDate),
				 Map.entry("reference_number", "CONCIAT-" +  resultMap.get("id")),
				 Map.entry("line_items", getItemsMapped(resultMap.get("items").toString())),
			 	 Map.entry("notes", resultMap.get("notes")),
			 	 Map.entry("pricebook_id", getMigratedIdValue("product_price_list", resultMap.get("id_product_price_list"), "pricebook")),
			 	 Map.entry("salesperson_id", "5304025000000172001"),
			 	 Map.entry("is_inclusive_tax", false)
		        );
	
		 Map<String, Object> mutableMap = new HashMap<>(valuesMapped);
		 var adjustment = getCoalhoCheaseAdjustment(resultMap.get("items").toString());
			if (adjustment != 0) {
				mutableMap.put("adjustment", adjustment);
				mutableMap.put("adjustment_description", "Ajustement");
				mutableMap.put("notes", valuesMapped.get("notes") + " - Applied adjustment of "+adjustment+" in the recalculation value by weight of the grilling cheese br Tasty by lb curd Coalho Grilling Cheese BR Tasty by LB");
			}
		
		return mutableMap;
	}
	
	
	protected Double getCoalhoCheaseAdjustment(String itemsJsonStr) {
		List<Map<String, Object>> itemsInvoiceConciat = jsonToMap(itemsJsonStr);
		
		var adjustment = itemsInvoiceConciat
							 .stream()
							 .filter(itemInv -> COALHO_CHEESE_PRODUCT_ID.contains( itemInv.get("id_product").toString() ))
							 .map(itemInv -> itemInv.get("coalho_cheese_adjustment"))
							 .filter(value -> value instanceof Number)
							 .mapToDouble(value -> ((Number) value).doubleValue())
							 .sum();
		
		return adjustment;
	}
	
	private List<Map<String, Object>> getItemsMapped(String itemsJsonStr) {
		
		
		List<Map<String, Object>> items = jsonToMap(itemsJsonStr);
		
		var itemsMapped = items.stream().map(item -> {
			
			var isCoalhoCheeseValid = item.get("is_coalho_cheese_valid") != null && item.get("is_coalho_cheese_valid").toString().equals("1"); 
			var quantity = isCoalhoCheeseValid ? item.get("quantity_coalho") : item.get("quantity");
			var value_unit = isCoalhoCheeseValid ? item.get("value_unit_coalho") : item.get("value_unit");
			
			return Map.ofEntries(
	            		Map.entry("item_id", getMigratedIdValue("product", item.get("id_product"), "item")),
	            		Map.entry("rate", value_unit),
	            		Map.entry("quantity", quantity),
	            		Map.entry("item_total", item.get("value_total"))
					);
			})
			.filter(map -> (Double) map.get("quantity") > 0)
			.collect(Collectors.toList());
		
		return itemsMapped;
	}

}

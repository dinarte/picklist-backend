package com.quebecteh.modules.migrators.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.quebecteh.modules.commons.clients.api.zoho.connector.ZohoConnectorProperties;
import com.quebecteh.modules.migrators.repository.MigrationLogRepository;
import com.quebecteh.modules.migrators.service.JDBCSourceConnectionService;

import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class InvoicesMigrationController extends AbstractBooksMigtationDefaultColntroller {
	
	@Autowired EntityManager em;
	
	@Autowired JdbcTemplate jdbcTemplate;
	
	private List<String> updateBatchsList = new ArrayList<String>();
	
	private static final List<String> COALHO_CHEESE_PRODUCT_ID = List.of("1052","1116");
	
	private Map<Object,Object> salesRepMap = Map.of(
												26, "5304025000001880574", //Alexa Paris
												25, "5304025000001880578", //Teka Lisboa
												28, "5304025000003796480", //Pedro Forte
												24, "5304025000003796109", //Ronaldo Giusti
												22, "5304025000001461243", //Sergio Vechiatto
												37, "5304025000003110773", //Ofelia Urquizo
												40, "5304025000003293441", //Leticia Mendes
												41, "5304025000003791119" //Gabriela Mendonca
											);
	

	public InvoicesMigrationController(ZohoConnectorProperties connProperties,
			JDBCSourceConnectionService connectionService, MigrationLogRepository migrationLogRepository) {
		super(connProperties, connectionService, migrationLogRepository);
		
	}
	
	protected MigrationConfiguration getCofiguration() {
		
		return MigrationConfiguration.builder()
				.tenantId("bave-" + zohoOrganizationId)	
				.sourceAppName("conciat")
				.sourceEntity("invoice")
				.sourceFieldId("id")
				.destinationApp("zoho-inventory")
				.destinationEntity("invoice")
				.destinationFieldId("invoice_id")
				.destinationResource("invoices")
				.build();
	}
	
	
	protected String getSql(List<String> migratedIds) {
		
		String sqlMigratedsIds = "";
		if (migratedIds != null && migratedIds.size() > 0) {
			sqlMigratedsIds = migratedIds.stream().collect(Collectors.joining(","));
			sqlMigratedsIds = "AND i.id not in("+sqlMigratedsIds+")\r\n";
		}
		
		return 
				" with invoice_detail_tab as(\r\n"
				+ "	select	i.id_sales_order,\r\n"
				+ "			id.id_invoice,\r\n"
				+ "			id.id_product,\r\n"
				+ "			id.value_unit,\r\n"
				+ "			id.unit_cost, id.unit_direct_cost,\r\n"
				+ "			sum(id.total_weight) as weight,\r\n"
				+ "			sum(id.quantity) as quantity, \r\n"
				+ "			sum(id.value_total) as value_total, \r\n"
				+ "			(round(sum(id.total_weight) * id.value_unit,2) - sum(id.value_total) between -5 and 5) as is_coalho_cheese_valid,\r\n"
				+ "			sum(id.value_total) - round(sum(id.total_weight) * id.value_unit,2) as coalho_cheese_adjustment\r\n"
				+ "	from invoice_detail id\r\n"
				+ "	join invoice i on i.id = id.id_invoice\r\n"
				+ " where id.quantity > 0 \r\n"
				+ "	group by i.id_sales_order, id.id_invoice, id.id_product, id.value_unit, id.unit_cost, id.unit_direct_cost\r\n"
				+ ")\r\n"
				+ "select i.id, i.id_sales_order, so.id_customer, invoice_number, i.date_creation, \r\n"
				+ "	 DATE_FORMAT(i.date_payment_due, '%Y-%m-%d') as date_payment_due, \r\n"
				+ "	 DATE_FORMAT(i.date_estimated_delivery, '%Y-%m-%d') as date_estimated_delivery, \r\n"
				+ "	 DATE_FORMAT(i.invoice_date, '%Y-%m-%d') as invoice_date, \r\n"
				+ "	 i.notes_internal, i.notes_invoice, i.value_total_invoice, sp.id as sales_rsp_id, sp.name as sales_rsp, \r\n"
				+ "	 i.value_total_tax, i.value_shipping_total, i.value_product_total, i.value_total_invoice,\r\n"
				+ "	 JSON_ARRAYAGG(\r\n"
				+ "		JSON_OBJECT(\r\n"
				+ "			#'id', id.id,\r\n"
				+ "			'id_product', id.id_product,\r\n"
				+ "			'quantity', id.quantity,\r\n"
				+ "			'value_unit', id.value_unit,\r\n"
				+ "			'value_total', id.value_total,\r\n"
				+ "			'weight', id.weight,\r\n"
				+ "			'is_coalho_cheese_valid', id.is_coalho_cheese_valid,\r\n"
				+ "			'coalho_cheese_adjustment', coalho_cheese_adjustment\r\n"
				+ "		)\r\n"
				+ "	 ) as items\r\n"
				+ "from invoice i \r\n"
				+ "join invoice_detail_tab id on id.id_invoice = i.id\r\n"
				+ "join sales_order so on so.id = i.id_sales_order \r\n"
				+ "left join system_persona sp on sp.id = so.id_system_persona_sales_rep  \r\n"
				+ "where so.id_sales_order_status <> 5\r\n"
				+ "and i.value_total_invoice > 0\r\n"
				+ "and i.id_invoice_status <> 4\r\n"
				//+ "and i.invoice_number in 3029, 3054, 3161, 3424, 4031, 4133, 5429, 5998, 6041, 6078, 6128, 6244, 6261, 6760, 6266)\r\n"
				//+ "and i.invoice_number = 3161\r\n"
				+ sqlMigratedsIds
				+ "group by i.id\r\n"
				+ "order by invoice_date \r\n";
				//+ "LIMIT 1 OFFSET 0";
	}
	

	protected Map<String, Object> getMappedJson(Map<String, Object> resultMap) {
		
		log.info("Executing mapping: {}", resultMap.get("id"));
		
		String custumerId;
		try{
			custumerId = getMigratedIdValue("customer", resultMap.get("id_customer"), "contact");
		} catch (Exception e) {
			throw new RuntimeException("Zoho CustumerId no found for SalesOrder: " + resultMap.get("id_sales_order"), e);
		}
		
		String salesRepId = resultMap.get("sales_rsp_id") != null ? salesRepMap.get(resultMap.get("sales_rsp_id")).toString() : "5304025000001461243";
		
		 Map<String, Object> valuesMapped = Map.ofEntries(
				Map.entry("customer_id", custumerId),
				Map.entry("invoice_number", "CINV-" + resultMap.get("invoice_number")),
	            Map.entry("reference_number", "CONCIAT-" + resultMap.get("id")),
	            Map.entry("date", resultMap.get("invoice_date")),
	            Map.entry("payment_mode", "Bank Transfer"), 
	            //Map.entry("payment_terms_label", "Net 15"),
	            Map.entry("due_date", resultMap.get("date_payment_due")),
	            Map.entry("salesperson_id",  salesRepId ),
	            Map.entry("allow_partial_payments", true),
	            Map.entry("shipping_charge", resultMap.get("value_shipping_total")),
	            Map.entry("notes", "CSO-" + resultMap.get("id_sales_order") ),
	            Map.entry("terms", resultMap.get("notes_invoice")),
	            Map.entry("line_items", getItemsMapped(resultMap.get("id_sales_order").toString(), resultMap.get("items").toString()))   
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


	protected List<Map<String, Object>> getItemsMapped(Object sourceId, String itemsJsonStr) {
		
		
		List<Map<String, Object>> itemsInvoiceConciat = jsonToMap(itemsJsonStr);
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> itemsMigratedSO  = (List<Map<String, Object>>) getSuccessMigrationLog("sales_order", sourceId, "salesorder")
																				.getDestinationData().getData().get("line_items");
		
		
		
		var itemsMapped = itemsInvoiceConciat.stream().map(itemInv -> {
			
			var zohoItemId = getMigratedIdValue("product", itemInv.get("id_product"), "item");	
			var zohoSalesOrderItemId = itemsMigratedSO
											.stream()
											.filter(map -> map.get("item_id").equals(zohoItemId))
											.findFirst()
											.get()
											.get("line_item_id");
			
			var quantity = (Double) itemInv.get("quantity");
			var valueUnit = (Double) itemInv.get("value_unit"); 
			var idProduct = itemInv.get("id_product").toString();
			var weight = (Double) itemInv.get("weight");
			var isCualhoCheeseValueValid = (int)itemInv.get("is_coalho_cheese_valid")==1;
			
			if( COALHO_CHEESE_PRODUCT_ID.contains( idProduct ) && valueUnit <= 10 && isCualhoCheeseValueValid ) {
				quantity = weight;			
			}
			
				return Map.of(
					    "item_id", zohoItemId,
					    "salesorder_item_id", zohoSalesOrderItemId,
					    "rate", itemInv.get("value_unit"),
					    "quantity", quantity,
					    "item_total", itemInv.get("value_total"),
					    "track_batch_number", true
					    //"batches", List.of()
					    //"batches", getUnicBatchesMapped(itemSO.get("item_id").toString(), itemSO.get("quantity"))
					    );
				
		}).collect(Collectors.toList());
		
		return itemsMapped;
	}
	
	
	private List<Map<String, Object>> getItemsMapped(String itemsJsonStr) {
		
		
		List<Map<String, Object>> items = jsonToMap(itemsJsonStr);
		
		var itemsMapped = items.stream().map(item -> {
			return Map.ofEntries(
	            		Map.entry("item_id", getMigratedIdValue("product", item.get("id_product"), "item")),
	            		Map.entry("rate", item.get("value_unit")),
	            		Map.entry("quantity", item.get("quantity")),
	            		//Map.entry("unit", "qty"),
	            		//Map.entry("tax_id", 4815000000044043L),
	            		//Map.entry("tds_tax_id", "460000000017098"),   // É string no JSON
	            		//Map.entry("tax_name", "Sales Tax"),
	            		//Map.entry("tax_type", "tax"),
	            		//Map.entry("tax_percentage", 12),
	            		Map.entry("item_total", item.get("value_total"))
	            		//Map.entry("warehouse_id", 130426000000664020L),
	            		//Map.entry("hsn_or_sac", 80540),
	            		//Map.entry("sat_item_key_code", 71121206),
	            		//Map.entry("unitkey_code", "E48")
					);
					
					/*
					Map.of(
				    "item_id", getMigratedIdValue("product", item.get("id_product"), "item"),
				    "rate", item.get("value_unit"),
				    "quantity", item.get("quantity"),
				    //"discount", 0.00,
				    "item_total", item.get("value_total")
				    );*/
				
			}).collect(Collectors.toList());
		
		return itemsMapped;
	}
	
	
	
	private List<Map<String, Object>> getUnicBatchesMapped(String zohroItemId, Object quantity) {
			String batchNumber = "INITIAL-BATCH";
			Double qtdDouble = Double.parseDouble(quantity.toString());
			return getPurchaseOrderItemBatchIds(batchNumber, zohroItemId, qtdDouble.intValue() );
	}
	
	public List<Map<String,Object>> getPurchaseOrderItemBatchIds(String batchNumber, String zohoItemId, Integer quantity){
		
		String sql = "select receive_id, item_id, batch_number, batch_id, batch_in_id, in_quantity, balance "
				+ "from migrations.batchs \r\n"
				+ "where  balance> 0\r\n"
				+ "AND	  batch_number = '"+batchNumber+"'\r\n"
				+ "AND	  item_id	= '"+zohoItemId+"'\r\n"
				+ "order by date_r\r\n";
				
		
		var rows = jdbcTemplate.queryForList(sql);
		var batchsInList = getCalculedBatches(quantity, rows);
		updateBatchsList = batchsInList.stream().map( row -> {
			return "update migrations.batchs set balance = " + row.get("new_balance") + " where batch_in_id = '"+ row.get("batch_in_id")+"'";
		}).collect(Collectors.toList());
		return batchsInList;
	}

	public static List<Map<String, Object>> getCalculedBatches(Integer quantity, List<Map<String, Object>> rows) {
		
		var batchsInList = new ArrayList<Map<String,Object>>();
		var totalBalance = 0;
		var totalQuantity = 0;
		for (Map<String, Object> row : rows) {
			totalBalance += Integer.valueOf( row.get("balance").toString() );
			totalQuantity += Integer.valueOf( row.get("in_quantity").toString() );
			var newBalance = (totalBalance - quantity) > 0 ? (totalBalance - quantity) : 0;
			var itemMap = new HashMap<String, Object>();
			itemMap.put("batch_in_id", row.get("batch_in_id"));
			itemMap.put("new_balance", 0);
			itemMap.put("out_quantity", (totalBalance - quantity) > 0 ? quantity : totalBalance);
			batchsInList.add(itemMap);
			if (totalBalance >= quantity) {
				itemMap.put("new_balance", newBalance);
				itemMap.put("out_quantity", Integer.valueOf( row.get("balance").toString() ) - newBalance);
				//itemMap.put("out_quantity", quantity);
				break;
			}
			
		} 
		
		return batchsInList;
	}
	
	protected void migrationLogsOnCreateCallBack() {
		
		updateBatchsList.forEach(sql -> {
			jdbcTemplate.execute(sql);
		});
		
	}

}

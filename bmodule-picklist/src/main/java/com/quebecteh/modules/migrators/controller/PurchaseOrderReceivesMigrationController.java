package com.quebecteh.modules.migrators.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.quebecteh.modules.commons.clients.api.zoho.connector.ZohoConnectorProperties;
import com.quebecteh.modules.migrators.domain.MigrationLog;
import com.quebecteh.modules.migrators.repository.MigrationLogRepository;
import com.quebecteh.modules.migrators.service.JDBCSourceConnectionService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class PurchaseOrderReceivesMigrationController extends AbstractInventoryMigtationDefaultColntroller {

	
	@Autowired EntityManager em;
	
	public PurchaseOrderReceivesMigrationController(ZohoConnectorProperties connProperties,
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
			.destinationEntity("purchasereceive")
			.destinationFieldId("receive_id")
			.destinationResource("purchasereceives")
			.build();
	}
	
	
	protected String getSql(List<String> migratedIds) {
		
		String sqlMigratedsIds = "";
		if (migratedIds != null && migratedIds.size() > 0) {
			sqlMigratedsIds = migratedIds.stream().collect(Collectors.joining(","));
			sqlMigratedsIds = "AND po.id not in("+sqlMigratedsIds+")\r\n";
		}
		
		return 
				"select \r\n"
				+ "    po.id, po.po_number, \r\n"
				+ "    DATE_FORMAT(po.date_creation, '%Y-%m-%d') AS date_creation,\r\n"
				+ "    po.id_vendor,\r\n"
				+ "    JSON_ARRAYAGG(\r\n"
				+ "        JSON_OBJECT(\r\n"
				+ "        	'id', pod.id,\r\n"
				+ "            'id_purchase_order', pod.id_purchase_order,\r\n"
				+ "            'id_product', pod.id_product,\r\n"
				+ "            'quantity', pod.quantity,\r\n"
				+ "            'batches', (\r\n"
				+ "                select JSON_ARRAYAGG(\r\n"
				+ "                    JSON_OBJECT(\r\n"
				+ "                        'lot_number', concat(por.lot_number,' (',por.expiration_date,')'),\r\n"
				+ "                        'expiration_date', por.expiration_date,\r\n"
				+ "                        'quantity_receipt', por.quantity_receipt\r\n"
				+ "                    )\r\n"
				+ "                )\r\n"
				+ "                from purchase_order_receipt por\r\n"
				+ "                where por.id_purchase_order_detail = pod.id\r\n"
				+ "                  and por.is_active = 1\r\n"
				+ "                  and por.is_receipt = 1\r\n"
				+ "            )\r\n"
				+ "         )\r\n"
				+ "    ) as items\r\n"
				+ "from purchase_order po \r\n"
				+ "join purchase_order_detail pod on pod.id_purchase_order = po.id\r\n"
				+ "join products p on p.id = pod.id_product\r\n"
				+ "where po.id_purchase_order_status <> 6\r\n"
				+ "and po.is_active is true\r\n"
				+ "and pod.is_active is true\r\n"
				+ "and p.is_active is true\r\n"
				+ sqlMigratedsIds
				+ "\r\nGROUP BY \r\n"
				+ "    po.id \r\n";
				//+ "LIMIT 20 OFFSET 0";
	}
	
	
	protected Map<String, Object> getMappedJson(Map<String, Object> resultMap) {
		
		log.info("Executing mapping: {}", resultMap.get("id"));
		
		String zohoPurchaseorderId = getMigratedIdValue("purchaseorders", resultMap.get("id"), "purchaseorder");
		String zohoVendorId = getMigratedIdValue("vendor", resultMap.get("id_vendor"), "contact");
		
		this.adtionalParams.put("purchaseorder_id", zohoPurchaseorderId);
		 
		Map<String, Object> valuesMapped = Map.ofEntries(
			Map.entry("date", resultMap.get("date_creation")),
			Map.entry("receive_number", "CPR_"+resultMap.get("po_number")),
			Map.entry("vendor_id", zohoVendorId),
			Map.entry("notes", ""),
			Map.entry("line_items", getItemsMapped(resultMap.get("items").toString()))
		);
		
		return valuesMapped;
	}
	

	private List<Map<String, Object>> getItemsMapped(String itemsJsonStr) {
		
		List<Map<String, Object>> items = jsonToMap(itemsJsonStr);
		
		var itemsMapped = items.stream().map(item -> {
				String zohoItemId = getMigratedIdValue("product", item.get("id_product"), "item");
				String zohoPurchaseOrderLineItemId = getPurchaseOrderLineItemId(item.get("id_purchase_order"), zohoItemId); 
				String zohoBillLineItemId = getBillLineItemId(item.get("id_purchase_order"), zohoItemId);
				var batchMapped = Map.of(
					    "item_id", zohoItemId,
					    "line_item_id", zohoPurchaseOrderLineItemId,
					    "bill_line_item_id", zohoBillLineItemId,
					    "quantity", item.get("quantity"),
					    "track_batch_for_receive", true,
			            "track_batch_number", true,
			            "batches", getUnicBatchesMapped(zohoItemId, item.get("quantity"))
			            //"batches", getBatchesMapped((List<Map<String, Object>>) item.get("batches"))
					    );
				return batchMapped;
			}).collect(Collectors.toList());
		
		return itemsMapped;
	}
	
	
	private List<Map<String, Object>> getUnicBatchesMapped(String zohroItemId, Object quantity) {
		
		String batchNumber = "INITIAL-BATCH";
		
		
		var batchMap = new HashMap<String, Object>();
		batchMap.put("batch_number", batchNumber);
		batchMap.put("in_quantity", quantity);
		
		Map<String, Object> batchIds = getPurchaseOrderItemBatchIds(batchNumber, zohroItemId);
		if (batchIds != null && !batchIds.isEmpty()) {
			batchMap.put("batch_id", batchIds.get("batch_id"));
			//batchMap.put("batch_in_id", batchIds.get("batch_in_id"));
		}
		
		List<Map<String, Object>> itemsMapped = List.of(batchMap);
		
		return itemsMapped;
	}
	
	
	@SuppressWarnings("unused")
	private List<Map<String, Object>> getBatchesMapped(List<Map<String, Object>> batches) {
		
		var itemsMapped = batches.stream().map(batch -> {
			return Map.of(
				    "batch_number", batch.get("lot_number"),
				    "expiry_date", batch.get("expiration_date"),
				    "in_quantity", batch.get("quantity_receipt")
				    );
				
			}).collect(Collectors.toList());
		
		return itemsMapped;
	}
	
	@SuppressWarnings("unchecked")
	private String getPurchaseOrderLineItemId(Object purchaseOrderId, String itemId) {
		
		MigrationLog purchaseOrderLog = getSuccessMigrationLog("purchaseorders", purchaseOrderId, "purchaseorder");
		var purchaseOrderLineItems  = (List<Map<String, Object>>) purchaseOrderLog.getDestinationData().getData().get("line_items");
		
		String lineItemId = purchaseOrderLineItems.stream()
			    .filter(item -> itemId.equals(item.get("item_id")))             
			    .map(item -> (String) item.get("line_item_id"))                 
			    .findFirst()                                                    
			    .orElse(null);  
		
		return lineItemId;
	}
	
	@SuppressWarnings("unchecked")
	private String getBillLineItemId(Object purchaseOrderId, String itemId) {
		
		MigrationLog billLog = getSuccessMigrationLog("purchaseorders", purchaseOrderId, "bill");
		var billLineItems  = (List<Map<String, Object>>) billLog.getDestinationData().getData().get("line_items");
		
		String lineItemId = billLineItems.stream()
			    .filter(item -> itemId.equals(item.get("item_id")))             
			    .map(item -> (String) item.get("line_item_id"))                 
			    .findFirst()                                                    
			    .orElse(null);  
		
		return lineItemId;
	}
	

	private Map<String,Object> getPurchaseOrderItemBatchIds(String batchNumber, String zohoItemId){
		String sql = "SELECT DISTINCT\r\n"
				+ "	   	  line_item->>'item_id'	   AS item_id,\r\n"
				+ "	   	  batch->>'batch_number'   AS batch_number,\r\n"
				+ "   	  batch->>'batch_id'       AS batch_id\r\n"
				+ "FROM   migrations.destination_data dd\r\n"
				+ "JOIN   migrations.migration_log ml on ml.id = dd.migration_log_id \r\n"
				+ "CROSS  JOIN LATERAL jsonb_array_elements(dd.data->'line_items') line_item\r\n"
				+ "CROSS  JOIN LATERAL jsonb_array_elements(line_item->'batches') batch\r\n"
				+ "WHERE  ml.destination_entity = 'purchasereceive'\r\n"
				+ "AND	  ml.status = 'success'\r\n"
				+ "AND	  batch->>'batch_number' = '"+batchNumber+"'\r\n"
				+ "AND	  line_item->>'item_id'	= '"+zohoItemId+"'";
		
		Map<String, Object> resultMap = null;
		
		Object[] result = null; 
		
		try {
			result = (Object[]) em.createNativeQuery(sql).getSingleResult();
			resultMap = Map.of(
							"item_id", result[0],
							"batch_number", result[1],
							"batch_id", result[2]
						);
		}catch (NoResultException e) {
			// TODO: nothing to do;
		}	
		
		return resultMap;
	}

}

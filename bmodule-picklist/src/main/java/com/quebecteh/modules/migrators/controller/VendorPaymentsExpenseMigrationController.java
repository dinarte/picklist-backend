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
public class VendorPaymentsExpenseMigrationController extends AbstractBooksMigtationDefaultColntroller {

	
	public VendorPaymentsExpenseMigrationController(ZohoConnectorProperties connProperties,
			JDBCSourceConnectionService connectionService, MigrationLogRepository migrationLogRepository) {
		super(connProperties, connectionService, migrationLogRepository);
		
	}


	protected MigrationConfiguration getCofiguration() {
		
		return MigrationConfiguration.builder()
			.tenantId("bave-" + zohoOrganizationId)	
			.sourceAppName("conciat")
			.sourceEntity("expense_purchase_order_payment")
			.sourceFieldId("id")
			.destinationApp("zoho-inventory")
			.destinationEntity("vendorpayment")
			.destinationFieldId("payment_id")
			.destinationResource("vendorpayments")
			.build();
	}
	
	
	protected String getSql(List<String> migratedIds) {
		
		String sqlMigratedsIds = "";
		if (migratedIds != null && migratedIds.size() > 0) {
			sqlMigratedsIds = migratedIds.stream().collect(Collectors.joining(","));
			sqlMigratedsIds = "AND pop.id not in("+sqlMigratedsIds+")\r\n";
		}
		
		return 
				" select\r\n"
				+ "	pop.id,  \r\n"
				+ "	pop.id_expense,\r\n"
				+ "	e.id as id_expense,\r\n"
				+ "	e.id_vendor,\r\n"
				+ "	DATE_FORMAT(pop.date_payment, '%Y-%m-%d') as date_payment,\r\n"
				+ "	pop.value_paid \r\n"
				+ "from purchase_order_payment pop\r\n"
				+ "join expenses e on e.id = pop.id_expense \r\n"
				+ "where pop.is_active is true \r\n"
				+ "and pop.value_paid > 0 \r\n"
				+ "and pop.date_payment < '2025-01-01'\r\n"
				+ "and pop.status =2\r\n"
				+ "and pop.id_payment_type=43\r\n"
				+ "and e.id_po is not null\r\n"
				+ "and e.id_po <> 0\r\n"
				+ "and e.is_active is true\r\n"
				//+ "and pop.id = 1878\r\n"
				+ sqlMigratedsIds
				+ "order by pop.id_expense, 5 \r\n";
				//+ " LIMIT 3 OFFSET 0";
	}
	
	protected Map<String, Object> getMappedJson(Map<String, Object> resultMap) {
		
		log.info("Executing mapping: {}", resultMap.get("id"));
		
		String vendorId = getMigratedIdValue("vendor", resultMap.get("id_vendor"), "contact");
		String billId = getMigratedIdValue("expenses", resultMap.get("id_expense"), "bill");
		 
		Map<String, Object> valuesMapped = Map.ofEntries(
				
				Map.entry("vendor_id", vendorId),

	            Map.entry("bills", List.of(
	                Map.ofEntries(
	                    Map.entry("bill_id", billId),
	                    Map.entry("amount_applied", resultMap.get("value_paid"))
	                    //Map.entry("tax_amount_withheld", 0.1)
	                )
	            )),

	            Map.entry("date", resultMap.get("date_payment")),
	           // Map.entry("exchange_rate", 1),
	            Map.entry("amount", resultMap.get("value_paid")),
	            Map.entry("paid_through_account_id", "5304025000000420021"),
	            Map.entry("payment_mode", "Stripe"),
	            Map.entry("description", "string"),
	            Map.entry("reference_number", "CONCIAT_"+resultMap.get("id")),
	            Map.entry("is_paid_via_print_check", false)
	        );
		
		return valuesMapped;
	}

}

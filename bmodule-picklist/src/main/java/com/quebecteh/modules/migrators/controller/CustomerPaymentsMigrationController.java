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
public class CustomerPaymentsMigrationController extends AbstractBooksMigtationDefaultColntroller {

	
	private final String BANCK_ACCOUNT = "5304025000000420021";
	private final String PETTY_CASH_ACCOUNT = "5304025000000000361";
	private final Integer CUSTOMER_CREDIT = 16;
	
	
	public CustomerPaymentsMigrationController(ZohoConnectorProperties connProperties,
			JDBCSourceConnectionService connectionService, MigrationLogRepository migrationLogRepository) {
		super(connProperties, connectionService, migrationLogRepository);

	}

	
	public MigrationConfiguration getCofiguration() {
		
		return MigrationConfiguration.builder()
			.tenantId("bave-" + zohoOrganizationId)	
			.sourceAppName("conciat")
			.sourceEntity("invoice_payment")
			.sourceFieldId("id")
			.destinationApp("zoho-inventory")
			.destinationEntity("payment")
			.destinationFieldId("payment_id")
			.destinationResource("customerpayments")
			.build();
	}
	
	public String getSql(List<String> migratedIds) {
		
		String sqlMigratedsIds = "";
		if (migratedIds != null && migratedIds.size() > 0) {
			sqlMigratedsIds = migratedIds.stream().collect(Collectors.joining(","));
			sqlMigratedsIds = "and ip.id not in("+sqlMigratedsIds+")\r\n";
		}
		
		return 
				"select	ip.id, ip.id_invoice, i.id_customer, i.value_total_paid, DATE_FORMAT(ip.date_payment , '%Y-%m-%d') as date_payment, \r\n"
				+ "		i.invoice_number, ip.id_payment_method, ip.value_paid_account, ip.notes \r\n"
				+ "				from	invoice_payment ip\r\n"
				+ "				join	invoice i on i.id = ip.id_invoice \r\n"
				+ "				join 	sales_order so on so.id = i.id_sales_order \r\n"
				+ "				where	so.id_sales_order_status <> 5\r\n"
				+ "				and		so.is_active is true \r\n"
				+ "				and		i.id_invoice_status <> 4\r\n"
				+ "				and 		ip.id_payment_type = 43\r\n"
				+ "				and		ip.date_payment < '2025-01-01'\r\n"
				+ 				sqlMigratedsIds
				+ "				order by ip.date_payment\r\n";
				

	}
	

	public Map<String, Object> getMappedJson(Map<String, Object> resultMap) {
		
		 log.info("Executing mapping: {}", resultMap.get("id"));
		 String idCustomer = getMigratedIdValue("customer", resultMap.get("id_customer"), "contact");
		 String idInvoice = getMigratedIdValue("invoice", resultMap.get("id_invoice"), "invoice");
		 
		 String paymentMode = ((Integer) resultMap.get("id_payment_method")).equals(CUSTOMER_CREDIT) ? "Cash" : "Bank Transfer";
		 String accountId = ((Integer) resultMap.get("id_payment_method")).equals(CUSTOMER_CREDIT) ? PETTY_CASH_ACCOUNT : BANCK_ACCOUNT;
		 
		 Map<String, Object> valuesMapped = Map.ofEntries(
				 Map.entry("customer_id", idCustomer),
		            Map.entry("payment_mode", paymentMode),
		            Map.entry("amount", resultMap.get("value_paid_account")),
		            Map.entry("date", resultMap.get("date_payment")),
		            Map.entry("reference_number", "CONCIAT-"+resultMap.get("id")),
		            Map.entry("description", "Payment has been added to CINV-"+resultMap.get("invoice_number")),
		            Map.entry("account_id", accountId),
		            Map.entry("notes", resultMap.get("notes")),
		            Map.entry("invoices", List.of(
		            		Map.ofEntries(
			                		Map.entry("invoice_id", idInvoice),
			                		Map.entry("amount_applied", resultMap.get("value_paid_account"))
			                )       
			        ))
		            //Map.entry("tax_account_id", ""),
		            
		        );

		
		return valuesMapped;
	}
	
	

}

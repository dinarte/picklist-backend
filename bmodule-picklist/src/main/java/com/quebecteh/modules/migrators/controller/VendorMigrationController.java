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
public class VendorMigrationController extends AbstractInventoryMigtationDefaultColntroller {


	
	public VendorMigrationController(ZohoConnectorProperties connProperties,
			JDBCSourceConnectionService connectionService, MigrationLogRepository migrationLogRepository) {
		super(connProperties, connectionService, migrationLogRepository);
		// TODO Auto-generated constructor stub
	}


	public MigrationConfiguration getCofiguration() {
		
		return MigrationConfiguration.builder()
			.tenantId("bave-" + zohoOrganizationId)	
			.sourceAppName("conciat")
			.sourceEntity("vendor")
			.sourceFieldId("id")
			.destinationApp("zoho-inventory")
			.destinationEntity("contact")
			.destinationFieldId("contact_id")
			.destinationResource("contacts")
			.build();
	}
	
	
	public String getSql(List<String> migratedIds) {
		
		String sqlMigratedsIds = "";
		if (migratedIds != null && migratedIds.size() > 0) {
			sqlMigratedsIds = migratedIds.stream().collect(Collectors.joining(","));
			sqlMigratedsIds = "where v.id not in("+sqlMigratedsIds+")\r\n";
		}
		
		return 
				" select v.id, v.name, address, city, s.name as state, zipcode, phone, email, contact_name, contact_email, contact_phone, notes\r\n"
				+ "from vendor v\r\n"
				+ "left join state s on s.id = v.id_state "
				+ sqlMigratedsIds;
				//+ "LIMIT 5 OFFSET 0";
	}
	

	public Map<String, Object> getMappedJson(Map<String, Object> resultMap) {
		
		 log.info("Executing mapping: {}", resultMap.get("id"));
		 
		 Map<String, Object> valuesMapped = Map.ofEntries(
		            Map.entry("contact_name", resultMap.get("name")),
		            Map.entry("company_name", resultMap.get("name")),
		            Map.entry("payment_terms", 0),
		            //Map.entry("currency_id", 5755892000000000097L),
		            Map.entry("currency_id", "5304025000000000097"),
		            Map.entry("website", ""),
		            Map.entry("contact_type", "vendor"),
		            Map.entry("billing_address", Map.of(
		                "attention", resultMap.get("name"),
		                "address", resultMap.get("address"),
		                "street2", "",
		                "city", resultMap.get("city"),
		                "state", resultMap.get("state"),
		                "zip", resultMap.get("zipcode"),
		                "country", "U.S.A"
		            )),
		            Map.entry("shipping_address", Map.of(
	            		"attention", resultMap.get("name"),
		                "address", resultMap.get("address"),
		                "street2", "",
		                "city", resultMap.get("city"),
		                "state", resultMap.get("state"),
		                "zip", resultMap.get("zipcode"),
		                "country", "U.S.A"
		            )),
		            Map.entry("contact_persons", List.of(
		                Map.of(
		                    "salutation", "",
		                    "first_name", resultMap.get("contact_name"),
		                    "last_name", "",
		                    //"email", resultMap.get("contact_email"),
		                    "phone", resultMap.get("contact_phone"),
		                    "mobile", resultMap.get("contact_phone"),
		                    "is_primary_contact", true
		                )
		            )),
		            /*
		            Map.entry("default_templates", Map.of(
		                "invoice_template_id", 460000000052069L,
		                "invoice_template_name", "Custom Classic",
		                "estimate_template_id", 460000000000179L,
		                "estimate_template_name", "Service - Professional",
		                "creditnote_template_id", 460000000000211L,
		                "creditnote_template_name", "Fixed Cost - Professional",
		                "invoice_email_template_id", 460000000052071L,
		                "invoice_email_template_name", "Custom Invoice Notification",
		                "estimate_email_template_id", 460000000052073L,
		                "estimate_email_template_name", "Custom Estimate Notification",
		                "creditnote_email_template_id", 460000000052075L,
		                "creditnote_email_template_name", "Custom Credit Note Notification"
		            )),*/
		            Map.entry("language_code", "en"),
		            Map.entry("notes", resultMap.get("notes")),
		           
		            //Map.entry("tax_exemption_id", 11149000000061054L),
		            //Map.entry("tax_authority_id", 11149000000061052L),
		            //Map.entry("tax_id", 11149000000061058L),
		            //Map.entry("is_taxable", true),
		            Map.entry("facebook", ""),
		            Map.entry("twitter", "")
		            
		            //Map.entry("tax_authority_name", "string"),
		            //Map.entry("tax_exemption_code", "string")
		        );
		
		return valuesMapped;
	}
	 

}

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
public class CustomerMigrationController extends AbstractInventoryMigtationDefaultColntroller{

	
	
	public CustomerMigrationController(ZohoConnectorProperties connProperties,
			JDBCSourceConnectionService connectionService, MigrationLogRepository migrationLogRepository) {
		super(connProperties, connectionService, migrationLogRepository);
		
	}

	
	public MigrationConfiguration getCofiguration() {
		
		return MigrationConfiguration.builder()
			.tenantId("bave-" + zohoOrganizationId)	
			.sourceAppName("conciat")
			.sourceEntity("customer")
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
			sqlMigratedsIds = "where c.id not in("+sqlMigratedsIds+")\r\n";
		}
		
		return 
				" with unic_address_cte as (\r\n"
				+ "	select id_customer, id_custtomer_address_type, max(ca.id) as id \r\n"
				+ "	from customer_address ca \r\n"
				+ "	where is_active is true\r\n"
				+ "	group by id_customer, id_custtomer_address_type\r\n"
				+ "	order by 3 desc\r\n"
				+ ")\r\n"
				+ "select \r\n"
				+ "		c.id, c.business_name, c.notes,\r\n"
				+ "		bca.address as billing_address, bca.city as billing_city, bs.name as billing_state, bca.zipcode as billing_zipcode,  bcty.name as billing_country,\r\n"
				+ "		sca.address as shipping_address, sca.city as shipping_city, ss.name as shipping_state, sca.zipcode as shipping_zipcode,  scty.name as shipping_country,\r\n"
				+ "		cc.first_name, cc.last_name, cc.email, cc.phone \r\n"
				+ "from \r\n"
				+ "		customer c \r\n"
				+ "left join \r\n"
				+ "		customer_address bca \r\n"
				+ "			on bca.id_customer = c.id \r\n"
				+ "			and c.is_active is true \r\n"
				+ "			and bca.id_custtomer_address_type  = 28\r\n"
				+ "			and bca.id in (select cte.id from unic_address_cte cte where cte.id_custtomer_address_type = bca.id_custtomer_address_type)\r\n"
				+ "left join \r\n"
				+ "		state bs \r\n"
				+ "			on bs.id = bca.id_state \r\n"
				+ "left join \r\n"
				+ "		country bcty \r\n"
				+ "			on bcty.id  = bs.id_country \r\n"
				+ "left join \r\n"
				+ "		customer_address sca \r\n"
				+ "			on sca.id_customer = c.id \r\n"
				+ "			and c.is_active is true \r\n"
				+ "			and sca.id_custtomer_address_type  = 27\r\n"
				+ "			and sca.id in (select cte.id from unic_address_cte cte where cte.id_custtomer_address_type = sca.id_custtomer_address_type)\r\n"
				+ "left join \r\n"
				+ "		state ss \r\n"
				+ "			on ss.id = sca.id_state \r\n"
				+ "left join \r\n"
				+ "		country scty \r\n"
				+ "			on scty.id  = ss.id_country \r\n"
				+ "left join \r\n"
				+ "		customer_contact cc \r\n"
				+ "		on cc.id_customer = c.id \r\n"
				+ "		and cc.is_active is true\r\n"
				+ "		and cc.is_default is true\r\n"
				+ sqlMigratedsIds;
				//+ "LIMIT 5 OFFSET 0";
	}
	

	public Map<String, Object> getMappedJson(Map<String, Object> resultMap) {
		
		 log.info("Executing mapping: {}", resultMap.get("id"));
		  
		 Map<String, Object> valuesMapped = Map.ofEntries(
		            Map.entry("contact_name", resultMap.get("business_name")),
		            Map.entry("company_name", resultMap.get("business_name")),
		            Map.entry("payment_terms", 0),
		            Map.entry("currency_id", "5304025000000000097"),
		            Map.entry("pricebook_id", "5304025000001587141"),
		            Map.entry("website", ""),
		            Map.entry("contact_type", "customer"),
		            Map.entry("billing_address", Map.of(
		                "attention", resultMap.get("business_name"),
		                "address", resultMap.get("billing_address"),
		                "street2", "",
		                "city", resultMap.get("billing_city"),
		                "state", resultMap.get("billing_state"),
		                "zip", resultMap.get("billing_zipcode"),
		                "country", "U.S.A"
		            )),
		            Map.entry("shipping_address", Map.of(
	            		"attention", resultMap.get("business_name"),
		                "address", resultMap.get("shipping_address"),
		                "street2", "",
		                "city", resultMap.get("shipping_city"),
		                "state", resultMap.get("shipping_state"),
		                "zip", resultMap.get("shipping_zipcode"),
		                "country", "U.S.A"
		            )),
		            Map.entry("contact_persons", List.of(
		                Map.of(
		                    "salutation", "",
		                    "first_name", resultMap.get("first_name"),
		                    "last_name", resultMap.get("last_name"),
		                    "email", resultMap.get("email"),
		                    "phone", resultMap.get("phone"),
		                    "mobile", resultMap.get("phone"),
		                    "is_primary_contact", true
		                )
		            )),
		          
		            Map.entry("language_code", "en"),
		            Map.entry("notes", resultMap.get("notes")),
		           
		            Map.entry("facebook", ""),
		            Map.entry("twitter", "")
		            
		        );
		
		return valuesMapped;
	}
	 

	

}

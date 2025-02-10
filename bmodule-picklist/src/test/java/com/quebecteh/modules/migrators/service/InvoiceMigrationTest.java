package com.quebecteh.modules.migrators.service;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.quebecteh.modules.migrators.controller.InvoicesMigrationController;

public class InvoiceMigrationTest {
	
	
	
	@Test
	void test_getCalculedBatches() {
		
		List<Map<String, Object>> rows = 
				List.of( 
					Map.of(
							"batch_in_id", "1111111",
							"in_quantity", 50,
							"balance", 50
					),
					Map.of(
							"batch_in_id", "2222222",
							"in_quantity", 30,
							"balance", 30
					),
					Map.of(
							"batch_in_id", "3333333",
							"in_quantity", 80,
							"balance", 80
					)
		);
		
		//receive_id, item_id, batch_number, batch_id, batch_in_id, in_quantity, balance
		
		var result = InvoicesMigrationController.getCalculedBatches(90, rows);
		System.out.println(result);
		
		result.forEach( row -> {
			var sql = "update migrations.batchs set balance = " + row.get("new_balance") + " where batch_in_id = '"+ row.get("batch_in_id")+"'";
			System.out.println(sql);
		});
		
		
	}
	

}

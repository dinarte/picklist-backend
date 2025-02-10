package com.quebecteh.modules.migrators.service;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.quebecteh.modules.migrators.controller.CustomerMigrationController;
import com.quebecteh.modules.migrators.controller.CustomerPaymentsMigrationController;
import com.quebecteh.modules.migrators.controller.ExpensesCategoryToItemsMigrationController;
import com.quebecteh.modules.migrators.controller.ExpensesToBillsMigrationController;
import com.quebecteh.modules.migrators.controller.InvoicesMigrationController;
import com.quebecteh.modules.migrators.controller.ItemMigrationController;
import com.quebecteh.modules.migrators.controller.PurchaseOrderBillsMigrationController;
import com.quebecteh.modules.migrators.controller.PriceListMigrationController;
import com.quebecteh.modules.migrators.controller.ProvisionsToBillsMigrationController;
import com.quebecteh.modules.migrators.controller.PurchaseOrderMigrationController;
import com.quebecteh.modules.migrators.controller.PurchaseOrderReceivesMigrationController;
import com.quebecteh.modules.migrators.controller.SalesOrderMigrationController;
import com.quebecteh.modules.migrators.controller.VendorMigrationController;
import com.quebecteh.modules.migrators.controller.VendorPaymentsExpenseMigrationController;
import com.quebecteh.modules.migrators.controller.VendorPaymentsMigrationController;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootTest
@ActiveProfiles("local")
public class MigrationControllerTest {
	
	
	@Autowired
	ItemMigrationController itemsController;
	
	@Autowired
	PriceListMigrationController priceListMigrationController;
	
	@Autowired
	ExpensesCategoryToItemsMigrationController expensesCategoryToItemsMigrationController;
	
	@Autowired
	CustomerMigrationController customerController;
	
	@Autowired
	VendorMigrationController vendorController;
	
	@Autowired
	PurchaseOrderMigrationController purchaseOrderController;
	
	@Autowired
	PurchaseOrderBillsMigrationController poToBillsMigrationController;
	
	@Autowired
	PurchaseOrderReceivesMigrationController purchaseOrderReceivesMigrationController;
	
	@Autowired
	ProvisionsToBillsMigrationController provisionsToBillsMigrationController;
	
	@Autowired
	ExpensesToBillsMigrationController expensesToBillsMigrationController;
	
	@Autowired
	SalesOrderMigrationController salesOrderMigrationController;
	
	@Autowired
	VendorPaymentsMigrationController vendorPaymentsMigrationController;
	
	@Autowired
	VendorPaymentsExpenseMigrationController vendorPaymentsExpenseMigrationController;
	
	@Autowired
	InvoicesMigrationController invoiceMigrationController;
	
	@Autowired
	CustomerPaymentsMigrationController customerPaymentsMigrationController;
	
	
	@Test
	void itemsMigtaionTest() throws SQLException {
		itemsController.migrate();
	}
	
	@Test
	void priceListMigrationTest() throws SQLException {
		priceListMigrationController.migrate();
	}
	
	@Test
	void expensesCategoryToItemsMigtaionTest() throws SQLException {
		expensesCategoryToItemsMigrationController.migrate();
	}
	
	
	@Test
	void vendorMigtaionTest() throws SQLException {
		vendorController.migrate();
	}
	
	@Test
	void purchaseOrderMigtaionTest() throws SQLException {
		purchaseOrderController.migrate();
	}
	
	@Test
	void poToBillsMigrationTest() throws SQLException {
		poToBillsMigrationController.migrate();
	}
	
	@Test
	void purchaseOrderReceivesMigtaionTest() throws SQLException {
		purchaseOrderReceivesMigrationController.migrate();
	}
	
	@Test
	void expensesToBillsMigrationTest() throws SQLException {
		expensesToBillsMigrationController.migrate();
	}
	
	@Test
	void vendorPaymentsMigrationTest() throws SQLException {
		vendorPaymentsMigrationController.migrate();
	}
	
	@Test
	void vendorPaymentsExpenseMigrationControllerTest() throws SQLException {
		vendorPaymentsExpenseMigrationController.migrate();
	}
	
	/*
	@Test
	void provisionsToBillsMigrationTest() throws SQLException {
		provisionsToBillsMigrationController.migrate();
	}*/
	
	@Test
	void customerMigtaionTest() throws SQLException {
		customerController.migrate();
	}
	
	@Test
	void salesOrderMigrationTest() throws SQLException {
		salesOrderMigrationController.migrate();
	}
	
	@Test
	void invoiceMigrationTest() throws SQLException {
		invoiceMigrationController.migrate();
	}
	
	
	@Test
	void getPurchaseOrderItemBatchIdsTest() throws SQLException {
	 System.out.println( invoiceMigrationController.getPurchaseOrderItemBatchIds("INITIAL-BATCH", "5304025000002402224", 260) );
	}
	
	@Test
	void customerPaymentsMigrationControllerTest() throws SQLException {
		customerPaymentsMigrationController.migrate();
	}

}

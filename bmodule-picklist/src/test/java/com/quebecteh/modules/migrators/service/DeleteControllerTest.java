package com.quebecteh.modules.migrators.service;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.quebecteh.modules.migrators.controller.CustomerPaymentsDELETEController;
import com.quebecteh.modules.migrators.controller.InvoicesDELETEController;
import com.quebecteh.modules.migrators.controller.SalesOrdersDELETEController;
import com.quebecteh.modules.migrators.controller.SalesOrdersUPDATEController;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootTest
@ActiveProfiles("local")
public class DeleteControllerTest {
	
	@Autowired
	CustomerPaymentsDELETEController customerPaymentDeleteController;
	
	@Autowired
	InvoicesDELETEController invoicesDeleteController;
	
	@Autowired
	SalesOrdersDELETEController salesOrdersDELETEController;
	
	
	
	@Test
	void customerPaymentDeleteController_deleteAll_test() throws SQLException{
		customerPaymentDeleteController.deleteAll();
	}
	
	@Test
	void invoicesDeleteController_deleteAll_test() throws SQLException {
		invoicesDeleteController.deleteAll();
	}
	
	@Test
	void salesOrdersDELETEController_deleteCoalhoCheese_test() throws SQLException {
		salesOrdersDELETEController.deleteForCoalhoCheese();
	}
	
	@Test
	void invoicesDeleteController_deleteCoalhoCheese_test() throws SQLException {
		invoicesDeleteController.deleteForCoalhoCheese();
	}
	

}

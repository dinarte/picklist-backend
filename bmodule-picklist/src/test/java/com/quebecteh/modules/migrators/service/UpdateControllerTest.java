package com.quebecteh.modules.migrators.service;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.quebecteh.modules.migrators.controller.CustomerUPDATEController;
import com.quebecteh.modules.migrators.controller.InvoiceUPDATEController;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootTest
@ActiveProfiles("local")
public class UpdateControllerTest {
	
		
	@Autowired
	InvoiceUPDATEController invoiceUPDATEController;
	
	@Autowired
	CustomerUPDATEController customerUPDATEController;
	
	
	@Test
	void invoiceUPDATEController_updateAllforDiffFix_test() throws SQLException{
		invoiceUPDATEController.updateAllforDiffFix();
	}
	
	@Test
	void customerUPDATEController_updateAreaAndSalesResp() throws SQLException{
		customerUPDATEController.updateAreaAndSalesResp();
	}
	

}

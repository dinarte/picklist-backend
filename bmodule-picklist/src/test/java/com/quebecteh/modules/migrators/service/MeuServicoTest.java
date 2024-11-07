package com.quebecteh.modules.migrators.service;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.quebecteh.modules.migrators.seervice.JDBCSourceLoadModelsService;

import lombok.extern.log4j.Log4j2;

@Log4j2
//@SpringBootTest
//@ActiveProfiles("test")
class MeuServicoTest {

	@Autowired
	private JDBCSourceLoadModelsService loadModulesService;
	
	@Autowired
	private JDBCSourceModelService jdbcSourceModelService;
	
	
    @Test
    void testeMetodoDoServico() {
       log.info("Starting to loading modules from data base");
       var modules = loadModulesService.loadModels("Bave", "consiat");
        
       assertNotNull(modules);
       
       assertAll(
   		    "Validating all modules",
   		    modules.stream()
   		        .map(module -> () -> assertAll(
   		            "Module: " + module.getName(),
   		            () -> assertNotNull(module.getTenantId(), "TenantId should be set for module " + module.getName()),
   		            () -> assertAll(
   		                "Fields in module " + module.getName(),
   		                module.getFields().stream()
   		                    .map(field -> () -> assertEquals(
   		                        module.getTenantId(),
   		                        field.getTenantId(),
   		                        "Field tenantId should be equal to Module tenantId for field: " + field.getName()
   		                    ))
   		            )
   		        ))
       );
       
       jdbcSourceModelService.saveAll(modules);
       
       var allList = jdbcSourceModelService.findAll();
       assertNotNull(allList);
       assertEquals(modules.size(), allList.size());
       
       
       
    }
    
    
 
}

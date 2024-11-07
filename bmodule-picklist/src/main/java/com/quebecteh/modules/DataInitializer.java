package com.quebecteh.modules;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.quebecteh.modules.inventary.picklist.model.domain.PickListTenant;
import com.quebecteh.modules.inventary.picklist.model.domain.PickListUser;
import com.quebecteh.modules.inventary.picklist.service.PickListTenantService;
import com.quebecteh.modules.inventary.picklist.service.PickListUserService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

	
	private final PickListUserService userService;
	
	private final PickListTenantService tenantService;

	@Bean
	public CommandLineRunner loadData() {
		return args -> {
			createSysAdminUser();
			createBaveTenat();
		};
	}
	
	private void createBaveTenat() {
		var count = tenantService.countByTenantId("Bave");
		if (count == 0) {
			var tenant = PickListTenant
								.builder()
								.tenantId("bave")
								.tenantName("Bave USA LLC")
								.build();
			tenantService.saveOrUpdate(tenant);
		}
	}

	private void createSysAdminUser() {
		Long countAdmin = userService.countBy("roles", "SysAdmin");
		if (countAdmin == 0) {
			var user = PickListUser.builder().email("dinarte@gmail.com").name("SysAdmin").password("123456")
					.roles("sysAdmin").tenantId("all").build();
			userService.saveOrUpdate(user);
		}
	}
}

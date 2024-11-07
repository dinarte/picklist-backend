package com.quebecteh.modules.inventary.picklist.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder 
public class PickListItemDTO {
	
	private Long Id;
	
	private Long pickListId;
	
	private String pickListRouteCode;
	
	private String pickListRouteName;
	
	private String pickListStaus;
	
	private String itemId;
	
	private String productCode;
	
	private String productName;
	
	private String productUnitType;
	
	private Double quantityInUnit;
	
	private Integer quantityOrdered;
	
	private Integer quantityDisponible;
	
	private Integer quantityPicked;
	
	@Builder.Default
	@Pattern(regexp = "Waiting|In Progress|Picked|Out of Stock|Canceled")
	private String status = "Waiting"; //Waiting, In Progress, Complete, Out of Stock, Canceled;
	
	@NotNull @NotEmpty
	private String routeCode;
	
	private String routeName;
	
	private String salesOrderId;
	
	private String salesOrderCode;
	
	private String salesOrderDescription;
	
	private String salesOrderCustomerId;
	
	private String salesOrderCustomerCode;
	
	private String salesOrderCustomerName;
	
	@NotNull @NotEmpty
	private String tenantId;
		
}

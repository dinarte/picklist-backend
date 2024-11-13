package com.quebecteh.modules.inventary.picklist.model.dto;

import java.util.ArrayList;
import java.util.List;

import com.quebecteh.modules.inventary.picklist.model.domain.PickListItem;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder 
public class PickListItemGrupedDTO {
	
	@Builder.Default
	private List<PickListItem> itemList = new ArrayList<PickListItem>();
	
	private Long pickListId;
	
	private String itemId;
	
	private String productCode;
	
	private String productName;
	
	private String barCode;
	
	private String productImage;
	
	private String productUnitType;
	
	private Double quantityInUnit;
	
	private Integer quantityOrdered;
	
	private Integer quantityDisponible;
	
	private Integer quantityPicked;

	private String status;
	
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

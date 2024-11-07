package com.quebecteh.modules.inventary.picklist.model.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.quebecteh.modules.inventary.picklist.PickListConstants;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder 
@Entity
@Table(name = "pick_list_item", schema = "bmodules-picklist")
public class PickListItem {

	@JsonProperty("id")
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;
	
	@JsonBackReference
	@ManyToOne
	private PickList pickList;
	
	private String itemId;
	
	private String productCode;
	
	private String barCode;
	
	private String productImage;
	
	private String productName;
	
	private String productUnitType;
	
	private Double quantityInUnit;
	
	private Integer quantityOrdered;
	
	private Integer quantityDisponible;
	
	private Integer quantityPicked;
	
	@Builder.Default
	@Pattern(regexp = PickListConstants.PICKLIST_ITEMS_AVAILABLE_STATUS)
	private String status = PickListConstants.PICKLIST_ITEMS_DEFAULT_STATUS;
	
	@NotNull @NotEmpty
	private String routeCode;
	
	private String routeName;
	
	private String salesOrderId;
	
	private String salesOrderCode;
	
	private String salesOrderDescription;
	
	private String salesOrderCustomerId;
	
	private String salesOrderCustomerCode;
	
	private String salesOrderCustomerName;
	
	@Builder.Default
	private LocalDateTime createdAt = LocalDateTime.now();
	
	@Builder.Default
	private LocalDateTime updatedAt = LocalDateTime.now();
	
	private String createdById;
	
	private String createdByName;
	
	private String updatedById;
	
	private String updatedByName;
	
	@NotNull @NotEmpty
	private String tenantId;
		
}

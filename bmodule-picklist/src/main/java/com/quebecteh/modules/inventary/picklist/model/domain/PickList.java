package com.quebecteh.modules.inventary.picklist.model.domain;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.quebecteh.modules.inventary.picklist.PickListConstants;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "pick_list", schema = "bmodules-picklist")
public class PickList {
	
	@JsonProperty("id")
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String code;
	
	@NotNull @NotEmpty
	private String routeCode;
	
	private String routeName;
	
	@Builder.Default
	@Pattern(regexp = PickListConstants.PICKLIST_AVAILABLE_STATUS)
	private String status = PickListConstants.PICKLIST_DEFAULT_STATUS;
	
	private String signedToId;
	
	private String signedToName;
	
	@Builder.Default
	private LocalDateTime createdAt = LocalDateTime.now();
	
	@Builder.Default
	private LocalDateTime updatedAt = LocalDateTime.now();
	
	private String createdById;
	
	private String createdByName;
	
	private String updatedById;
	
	private String updatedByName;
	
	@NotNull @NotEmpty
	private String organizationId;
	
	private String tenantId;

	@JsonManagedReference
	@OneToMany(mappedBy = "pickList", cascade = CascadeType.ALL)
	private List<PickListItem> pickListItems;

}

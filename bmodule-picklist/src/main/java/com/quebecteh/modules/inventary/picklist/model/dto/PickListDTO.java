package com.quebecteh.modules.inventary.picklist.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder 
public class PickListDTO {
	
	private Long id;
	
	private String code;
	
	@NotNull @NotEmpty
	private String routeCode;
	
	private String routeName;
	
	@Builder.Default
	@Pattern(regexp = "Open|Pending|In Progress|Done|Canceled")
	private String status = "Open";
	
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
	
	private String tenantId;

	private List<PickListItemDTO> pickListItems;

}

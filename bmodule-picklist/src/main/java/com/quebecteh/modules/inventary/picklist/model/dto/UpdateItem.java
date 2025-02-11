package com.quebecteh.modules.inventary.picklist.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateItem{	
	Long id;
	Integer quantityPicked;
}
package com.quebecteh.modules.inventary.picklist.model.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor
public class UpdateItemListDTO {
	
	private List<UpdateItem> updateList;
	
	
}



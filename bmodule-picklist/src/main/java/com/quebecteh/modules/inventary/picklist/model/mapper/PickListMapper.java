package com.quebecteh.modules.inventary.picklist.model.mapper;

import com.quebecteh.modules.inventary.picklist.model.domain.PickList;
import com.quebecteh.modules.inventary.picklist.model.dto.PickListDTO;

//@Mapper(componentModel = "spring") 
public interface PickListMapper {
	
	PickList toEntity(PickListDTO dto); 
	
	PickListDTO toDto(PickList entity); 

}

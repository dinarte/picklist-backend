package com.quebecteh.modules.inventary.picklist.model.mapper;

import com.quebecteh.modules.inventary.picklist.model.domain.PickList;
import com.quebecteh.modules.inventary.picklist.model.dto.PickListDTO;

public class PickListMapperImpl implements PickListMapper{

	@Override
	public PickList toEntity(PickListDTO dto) {
		
		return PickList
			.builder()
			.code(dto.getCode())
			.createdAt(dto.getCreatedAt())
			.createdById(dto.getCreatedById())
			.createdByName(dto.getCreatedByName())
			.id(dto.getId())
			.build();
		
		//return null;
	}

	@Override
	public PickListDTO toDto(PickList entity) {
		// TODO Auto-generated method stub
		return null;
	}

}

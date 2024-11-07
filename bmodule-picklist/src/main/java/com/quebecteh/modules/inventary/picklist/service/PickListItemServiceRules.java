package com.quebecteh.modules.inventary.picklist.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.quebecteh.commons.multitenancy.BusinessRulesEventTriggers;
import com.quebecteh.modules.inventary.picklist.model.domain.PickListItem;
import com.quebecteh.modules.inventary.picklist.validators.ValidationExceptionBuilder;

import lombok.extern.log4j.Log4j2;


@Component @Log4j2
public class PickListItemServiceRules implements BusinessRulesEventTriggers<PickListItem> {

		
	protected PickListItemService pickListItemService;
	
	public PickListItemServiceRules(PickListService pikListService, PickListItemService pickListItemService) {
		this.pickListItemService = pickListItemService;
		pickListItemService.setTrigger(this);
	}
	
	@Override
	public void beforeCreate(PickListItem item) {
		checkIfItemRouteCodeMatchesPickListRouteCode(item);
		checkIfThereIsAlreadyAnItemWithTheSameItemId(item, pickListItemService);
		setCreationMetaData(item);
		ValidationExceptionBuilder.build().throwsExcpeionIfHasErrors();
	}


	@Override
	public void beforeUpdata(PickListItem oldStateObj, PickListItem newStateObj) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void beforeDelete(PickListItem obj) {
		// TODO Auto-generated method stub
		
	}
	
	
    
    public void checkIfItemRouteCodeMatchesPickListRouteCode(PickListItem item) {
    	if (!item.getRouteCode().equals( item.getPickList().getRouteCode() )) {
			ValidationExceptionBuilder
			.add(item, "routeCode", "Only items belonging to the route " + item.getPickList().getRouteCode() + " are accepted in this picklist");
		}
    }
    
    public void checkIfThereIsAlreadyAnItemWithTheSameItemId(PickListItem item, PickListItemService pickListItemService) {
    
    	/*
    	Long count = pickListItemService.countWhere("pickList.id = "+item.getPickList().getId()+" and itemId = '" +item.getItemId()+ "'");
    	
    	if (count > 0) {
			ValidationExceptionBuilder
				.add(item, "itemId", "There is already an item with the same item ID");
		}*/
    }
    
   
    
    
    public void setCreationMetaData(PickListItem pickListItem) {
    	pickListItem.setCreatedAt(LocalDateTime.now());
    	pickListItem.setCreatedById("apiUnserId");
    	pickListItem.setCreatedById("Api User");
    	pickListItem.setUpdatedAt(LocalDateTime.now());
    	pickListItem.setUpdatedById("apiUnserId");
    	pickListItem.setUpdatedById("Api User");
    }


	

	



}


package com.quebecteh.modules.inventary.picklist.service;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.quebecteh.commons.multitenancy.BusinessRulesEventTriggers;
import com.quebecteh.modules.inventary.picklist.model.domain.PickList;
import com.quebecteh.modules.inventary.picklist.validators.ValidationExceptionBuilder;

import lombok.extern.log4j.Log4j2;


@Component @Log4j2
public class PickListServiceRules implements BusinessRulesEventTriggers<PickList> {

	
	protected PickListService pikListService;
	
	protected PickListItemService pickListItemService;
	
	protected PickListItemServiceRules pickListItemServiceRules;
	
	public PickListServiceRules(PickListService pikListService, PickListItemService pickListItemService, PickListItemServiceRules pickListItemServiceRules) {
		this.pikListService = pikListService;
		pikListService.setTrigger(this);
		this.pickListItemService = pickListItemService;
		this.pickListItemServiceRules = pickListItemServiceRules;
	}
	
	@Override
	public void beforeCreate(PickList obj) {
		createCodeIfCommingEmpty(obj, pikListService);
		checkIfRecordWithSameCodeExists(obj, pikListService);
		checkIfOpenPickListExistsForCode(obj, pikListService);
		validateItems(obj);
		copyRouteNameFromRouteCodeIfItsCommingEmpty(obj);
		setCreationMetaData(obj);
	}


	@Override
	public void beforeUpdata(PickList oldStateObj, PickList newStateObj) {
		validateItems(newStateObj);
	}


	@Override
	public void beforeDelete(PickList obj) {
		// TODO Auto-generated method stub
		
	}
	
	
	public void createCodeIfCommingEmpty(PickList pickList, PickListService pickListService) {
		if (pickList.getCode() == null || pickList.getCode() == "") {
			String code = pickList.getTenantId() + "|" + "PICKLIST-" + "" + pickListService.getNextSerialCode(4);
			pickList.setCode(code);
		}
	}
	
	public void checkIfRecordWithSameCodeExists(PickList pickList, PickListService pickListService) {
		
		long countByCode = pickListService.countBy("code", pickList.getCode());
		
		if (countByCode > 0) {
			
			ValidationExceptionBuilder
				.add(pickList, "code", "There is already picklist with the same code")
				.throwsExcpeionIfHasErrors();			
		}
	}
	

    public void validateItems(PickList pickList) {
    	if (Objects.isNull(pickList.getPickListItems())) {
    		return;
    	}
    	
    	pickList.getPickListItems().stream().forEach(item -> {
    		pickListItemServiceRules.beforeCreate(item);
    		pickListItemServiceRules.checkIfThereIsAlreadyAnItemWithTheSameItemId(item, pickListItemService);
    		pickListItemServiceRules.setCreationMetaData(item);
    	});
    	
    	ValidationExceptionBuilder.build().throwsExcpeionIfHasErrors();
    }
    
    public void checkIfOpenPickListExistsForCode(PickList pickList, PickListService pickListService) {
    	long totalFound = pickListService.countWhere("status = 'Open' and routeCode = '"+ pickList.getRouteCode() + "' ");
    	if (totalFound > 0) {
    		
    		ValidationExceptionBuilder
				.add(pickList, "routeCode", "There is already an open picklist for the route")
				.throwsExcpeionIfHasErrors();
    	}    
    }
    
    
    public void copyRouteNameFromRouteCodeIfItsCommingEmpty(PickList pickList) {
    	if (pickList.getRouteName() == null || pickList.getRouteName() == "") {
			pickList.setRouteName(pickList.getRouteCode());
		}
    }
    
    public void setCreationMetaData(PickList pickList) {
    	pickList.setCreatedAt(LocalDateTime.now());
    	pickList.setCreatedById("apiUnserId");
    	pickList.setCreatedById("Api User");
    	pickList.setUpdatedAt(LocalDateTime.now());
    	pickList.setUpdatedById("apiUnserId");
    	pickList.setUpdatedById("Api User");
    }


	

	



}


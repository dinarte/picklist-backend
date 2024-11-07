package com.quebecteh.modules.inventary.picklist.controller;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.quebecteh.commons.rest.ApiResponse;
import com.quebecteh.modules.commons.clients.api.zoho.model.ZohoResponse;
import com.quebecteh.modules.commons.connector.controller.ConnectionsController;
import com.quebecteh.modules.inventary.picklist.PickListConstants;
import com.quebecteh.modules.inventary.picklist.interceptors.RequiredTenatantId;
import com.quebecteh.modules.inventary.picklist.model.domain.PickList;
import com.quebecteh.modules.inventary.picklist.model.domain.PickListItem;
import com.quebecteh.modules.inventary.picklist.model.dto.TotalPickListByStatus;
import com.quebecteh.modules.inventary.picklist.service.PickListItemService;
import com.quebecteh.modules.inventary.picklist.service.PickListService;
import com.quebecteh.modules.inventary.picklist.service.ZohoInventarySalesOrderService;
import com.quebecteh.modules.inventary.picklist.validators.HttpResouceNotFoundException;
import com.quebecteh.modules.inventary.picklist.validators.ValidationExceptionBuilder;

/**
 * Controller for managing {@link PickList} resources for specific tenants.
 *
 * <p>This class provides RESTful endpoints to create, update, and retrieve PickList
 * objects associated with a tenant. Each method is mapped to an HTTP endpoint and
 * handles specific operations such as fetching a list of pick lists, retrieving a
 * single pick list, creating a new pick list, or updating an existing pick list.</p>
 *
 * <p>All methods that involve tenant or resource verification throw a custom exception
 * {@link HttpResouceNotFoundException} if the tenant or pick list resource is not found.</p>
 *
 * <p>The controller is annotated with {@code @RestController}, indicating that it handles HTTP
 * requests and returns responses in JSON format. HTTP status codes are explicitly defined for
 * each endpoint using {@code @ResponseStatus}.</p>
 */
@CrossOrigin(origins = "*")
@RestController
@RequiredTenatantId
public class PickListController {
    
	@Autowired
    PickListService pickListServive;
    
    @Autowired 
    PickListItemService pickListItemService;
    
    @Autowired
    ConnectionsController connections;
    
    @Autowired
    ZohoInventarySalesOrderService zohoSalesOrderService; 
    
    /**
     * Retrieves a list of all pick lists associated with the given tenant ID.
     *
     * <p>This endpoint is accessible via {@code GET /{tenantId}/picklist} and returns a list of
     * {@link PickList} objects that belong to the specified tenant. If the tenant ID is not found,
     * it throws a {@link HttpResouceNotFoundException}.</p>
     *
     * @param tenantId the ID of the tenant whose pick lists should be retrieved
     * @return a list of {@link PickList} objects for the tenant
     * @throws HttpResouceNotFoundException if the tenant ID does not exist
     */
    @GetMapping("/{tenantId}/pickList")
    @ResponseStatus(value = HttpStatus.OK)
    public List<PickList> list(@PathVariable("tenantId") String tenantId) throws HttpResouceNotFoundException {
        //verifyResouce(tenantId);
        return pickListServive.findByTenantId(tenantId);
    }
    
    /**
     * Retrieves a list of pick lists associated with the given tenant ID and status.
     *
     * <p>This endpoint is accessible via {@code GET /{tenantId}/pickList/status/{status}} and returns a
     * list of {@link PickList} objects that match the specified tenant ID and status. If the tenant ID
     * or status is not valid, it throws a {@link HttpResouceNotFoundException}.</p>
     * <p>List of valids status: {@link PickListConstants#PICKLIST_AVAILABLE_STATUS}</p>
     *
     * @param tenantId the ID of the tenant whose pick lists should be retrieved
     * @param status the status of the pick lists to retrieve, which must be one of the allowed values
     * @return a list of {@link PickList} objects that match the tenant ID and status
     * @throws HttpResouceNotFoundException if the tenant ID or status is invalid
     */
    @GetMapping("/{tenantId}/pickList/status/{status}")
    @ResponseStatus(value = HttpStatus.OK)
    public ApiResponse<List<PickList>> listByStatus(@PathVariable("tenantId") String tenantId, @PathVariable("status") String status) throws HttpResouceNotFoundException {
        //verifyResouce(tenantId);
        verifyStatus(status);
        var list = pickListServive.findAllWhere("tenantId = :tenantId and status = :status", 
        		Map.of(
        				"tenantId",tenantId,
        				"status",status)
        		);
        
        return new ApiResponse<List<PickList>>(
				HttpStatus.OK.value(), 
				"list-all-pickList-by-status-"+status,
				"List of all Pick Lists by "+status+ " status", 
				list);
    }
    

    
    
    @PatchMapping("/{tenantId}/pickList/{pickListId}/status/{status}")
    @ResponseStatus(value = HttpStatus.OK)
    public ApiResponse<PickList> setStatus(@PathVariable("tenantId") String tenantId, @PathVariable("pickListId") Long pickListId, @PathVariable("status") String status) throws HttpResouceNotFoundException {
    	verifyStatus(status);
    	//verifyResouce(tenantId);
    	verifyResouce(pickListId, tenantId);
  
    
    	pickListServive.updateField(pickListId, "status", status);
    	PickList picklist =  pickListServive.findById(pickListId).get();
    	
    	if (status.equals("Done")) {
    		         
            
            var salaesorderList = picklist
            		.getPickListItems()
					.stream()
        			.map(item -> Map.of("organizationId", item.getPickList().getOrganizationId(), "salesOrderId", item.getSalesOrderId()))
        			.collect(Collectors.toSet());

    		
            salaesorderList.forEach(item-> {
    			ZohoResponse response =  zohoSalesOrderService.setSalesOrderAsReadyToShipping(item.get("organizationId"), item.get("salesOrderId"));
    			if (response.getCode() !=0 ) {
    				ValidationExceptionBuilder.add(picklist, "status", response.getMessage()).throwsExcpeionIfHasErrors();
    			}
    		});
    		
    	}
    	
    	
    	return new ApiResponse<PickList>(
				HttpStatus.OK.value(), 
				"status-updated-"+status,
				"The Picklist status has been set to "+status, 
				picklist);
    	
    }
    
    
    @PatchMapping("/{tenantId}/pickList/{pickListId}/item/{itemId}/{status}")
    @ResponseStatus(value = HttpStatus.OK)
    public ApiResponse<PickListItem> setItemStatus(
    												@PathVariable("tenantId") String tenantId, 
    												@PathVariable("pickListId") Long pickListId, 
    												@PathVariable("itemId") Long itemId, 
    												@PathVariable("status") String status) throws HttpResouceNotFoundException {
    	verifyItemStatus(status);
    	//verifyResouce(tenantId);
    	verifyResouce(pickListId, tenantId);

    
    	pickListItemService.updateField(itemId, "status", status);
    	PickListItem picklistItem =  pickListItemService.findById(itemId).get();
    	
    	return new ApiResponse<PickListItem>(
				HttpStatus.OK.value(), 
				"status-updated-"+status,
				"The Picklist item status has been set to "+status, 
				picklistItem);
    	
    }


    /**
     * Retrieves a specific pick list for the given tenant and pick list ID.
     *
     * <p>This endpoint is accessible via {@code GET /{tenantId}/picklist/{id}} and returns a list of
     * {@link PickList} objects for the tenant. If the pick list or tenant ID is not found, it throws
     * a {@link HttpResouceNotFoundException}.</p>
     *
     * @param tenantId the ID of the tenant
     * @param id the ID of the pick list to retrieve
     * @return a list of {@link PickList} objects for the tenant
     * @throws HttpResouceNotFoundException if the pick list or tenant ID does not exist
     */
    @GetMapping("/{tenantId}/pickList/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public ApiResponse<PickList> get(@PathVariable("tenantId") String tenantId, @PathVariable("id") Long id) throws HttpResouceNotFoundException {
        verifyResouce(id, tenantId);
        var pickList = pickListServive.findOneBy("id", id);
        
        return new ApiResponse<PickList>(
				HttpStatus.OK.value(), 
				"successful-fetch-picklist",
				"Picklist succeffuly fetched", 
				pickList);
    }

    /**
     * Creates a new pick list for the specified tenant.
     *
     * <p>This endpoint is accessible via {@code POST /{tenantId}/picklist} and allows the creation
     * of a new {@link PickList} object for the tenant. The pick list is validated using {@code @Validated}.
     * The tenant ID is set on the pick list before saving. If the tenant ID is not found, a
     * {@link HttpResouceNotFoundException} is thrown.</p>
     *
     * @param pickList the pick list object to be created
     * @param tenantId the ID of the tenant to associate with the pick list
     * @return the created {@link PickList} object
     * @throws HttpResouceNotFoundException if the tenant ID does not exist
     */
    @PostMapping("/{tenantId}/pickList")
    @ResponseStatus(value = HttpStatus.CREATED)
    public PickList create(@RequestBody @Validated PickList pickList, @PathVariable("tenantId") String tenantId) throws HttpResouceNotFoundException {
        //verifyResouce(tenantId);
        pickList.setTenantId(tenantId);
        return pickListServive.saveOrUpdate(pickList);
    }

    /**
     * Updates an existing pick list for the specified tenant.
     *
     * <p>This endpoint is accessible via {@code PUT /{tenantId}/picklist/} and allows updating
     * of an existing {@link PickList} object. The pick list is validated using {@code @Validated}.
     * Before updating, the method verifies if both the pick list ID and tenant ID exist in the
     * system. If either the pick list or tenant ID is not found, a {@link HttpResouceNotFoundException}
     * is thrown.</p>
     *
     * @param pickList the pick list object to update
     * @param tenantId the ID of the tenant associated with the pick list
     * @return the updated {@link PickList} object
     * @throws HttpResouceNotFoundException if the pick list or tenant ID does not exist
     */
    @PutMapping("/{tenantId}/pickList")
    @ResponseStatus(value = HttpStatus.OK)
    public PickList update(@RequestBody @Validated PickList pickList, @PathVariable("tenantId") String tenantId) throws HttpResouceNotFoundException {
        verifyResouce(pickList.getId(), tenantId);
        pickList.setTenantId(tenantId);
        return pickListServive.saveOrUpdate(pickList);
    }
    
    
    
    /**
     * Adds items to a pick list for a specific tenant and route code.
     *
     * <p>This endpoint is accessible via {@code PUT /{tenantId}/picklist/itemsInRoute} and allows adding items
     * to an existing pick list with status 'Open' for the given tenant and route code, or creates a new pick list
     * if one does not exist. The pick list is validated using {@code @Validated}. The process is as follows:</p>
     *
     * <ul>
     *   <li><strong>Search for Existing Pick List:</strong> The method searches for an existing pick list where:
     *     <ul>
     *       <li>The tenant ID matches the provided {@code tenantId}.</li>
     *       <li>The route code matches {@code pickList.getRouteCode()}.</li>
     *       <li>The status is 'Open'.</li>
     *     </ul>
     *   </li>
     *   <li><strong>If Found:</strong>
     *     <ul>
     *       <li>Adds all items from the provided {@code pickList} to the existing pick list's items.</li>
     *       <li>Uses the existing pick list for saving or updating.</li>
     *     </ul>
     *   </li>
     *   <li><strong>If Not Found:</strong>
     *     <ul>
     *       <li>Sets the tenant ID of the provided {@code pickList} to the provided {@code tenantId}.</li>
     *       <li>Uses the provided pick list for saving or updating.</li>
     *     </ul>
     *   </li>
     *   <li><strong>Save or Update:</strong> The pick list (existing or new) is then saved or updated using
     *       {@code pickListService.saveOrUpdate(pickList)}.</li>
     * </ul>
     *
     * <p>This operation allows for efficient management of pick list items by either appending to an existing
     * pick list or creating a new one if necessary.</p>
     *
     * @param pickList the pick list containing items to add; must not be {@code null} and is validated
     * @param tenantId the ID of the tenant; must not be {@code null}
     * @return the updated or newly created {@link PickList} object
     * @throws HttpResouceNotFoundException if the tenant ID is not found
     */
    @PutMapping("/{tenantId}/pickList/itemsInRoute")
    @ResponseStatus(value = HttpStatus.OK)
    public ApiResponse<PickList> addItens(@RequestBody @Validated PickList pickList, @PathVariable("tenantId") String tenantId) throws HttpResouceNotFoundException {
        
    	var pickListExistent =  pickListServive.findFristWhere("tenantId=:tenantId and routeCode=:routCode and status='Open'", 
		    							Map.of(
	    									"routCode", pickList.getRouteCode(),
	    									"tenantId", tenantId
		    							)
        						 );
    	
    	if (pickListExistent == null) {
    		pickList.setTenantId(tenantId);
    		pickList = pickListServive.saveOrUpdate(pickList);
    		return new ApiResponse<PickList>(
    				HttpStatus.CREATED.value(), 
    				"a-new-pickList-for-the-route-has-been-opened-and-the-items-added", 
    				"A new pickList for the route "+pickList.getRouteCode()+" has been opened and the items added.", 
    				pickList);
    	} else {
    		List<PickListItem> items = pickList.getPickListItems(); 
    		items.stream().forEach(item -> {item.setPickList(pickListExistent);});
    		pickListItemService.saveOrUpdateAll(items);
    		return new ApiResponse<PickList>(
    				HttpStatus.ACCEPTED.value(), 
    				"the-items-have-been-added-to-the-existing-open-pickList-for-the-route", 
    				"The items have been added to the existing open pickList for the route " + pickList.getRouteCode(), 
    				pickListExistent);
    	}
       
    }
    
    
    @GetMapping("/{tenantId}/pickList/status/count")
    public ApiResponse<List<TotalPickListByStatus>> getTotalStatus(@PathVariable("tenantId") String tenantId) throws HttpResouceNotFoundException {
    	//verifyResouce(tenantId);
    	
    	var totalStatus = pickListServive.getTotalByStatus(tenantId);
    	
    	return new ApiResponse<List<TotalPickListByStatus>>(
				HttpStatus.OK.value(), 
				"Total-pick-lists-by-status", 
				"Total Pick Lists by status", 
				totalStatus);
    }
    
    
    /**
     * Verifies if the provided status is valid according to the allowed pick list status values.
     *
     * <p>This private method checks if the specified status matches one of the permitted values
     * defined in {@link PickListConstants#PICKLIST_AVAILABLE_STATUS}. If the status is invalid, an
     * {@link HttpResouceNotFoundException} is thrown with a descriptive error message.</p>
     *
     * @param status the status to validate
     * @throws HttpResouceNotFoundException if the status is invalid
     */
    private void verifyStatus(String status) throws HttpResouceNotFoundException {
        String regex = "^("+PickListConstants.PICKLIST_AVAILABLE_STATUS+")$";
        Pattern pattern = Pattern.compile(regex);
        if (!pattern.matcher(status).matches()) {
        	throw new HttpResouceNotFoundException(
        			"The status entered ("+status+") is not valid, valid values: "+ PickListConstants.PICKLIST_AVAILABLE_STATUS,
        			"invalid-status"
        	);
        }
	}
    
    private void verifyItemStatus(String status) throws HttpResouceNotFoundException {
        String regex = "^("+PickListConstants.PICKLIST_ITEMS_AVAILABLE_STATUS+")$";
        Pattern pattern = Pattern.compile(regex);
        if (!pattern.matcher(status).matches()) {
        	throw new HttpResouceNotFoundException(
        			"The status entered ("+status+") is not valid, valid values: "+ PickListConstants.PICKLIST_AVAILABLE_STATUS,
        			"invalid-status"
        	);
        }
	}
    

    /**
     * Verifies if a pick list exists for a given pick list ID and tenant ID.
     *
     * <p>This private method is used by the controller methods to check if the given pick list and
     * tenant combination exists in the system. If not, a {@link HttpResouceNotFoundException} is thrown.</p>
     *
     * @param pickListId the ID of the pick list
     * @param tenantId the ID of the tenant
     * @throws HttpResouceNotFoundException if the pick list or tenant is not found
     */
    protected void verifyResouce(Long pickListId, String tenantId) throws HttpResouceNotFoundException {
        long count = pickListServive.countWhere("tenantId = '"+tenantId+"' and id = " + pickListId);
        if (count <= 0) {
            throw new HttpResouceNotFoundException("pickList-id-not-found-for-the-tenantId", 
                    "PickList ID #"+pickListId+" not found for tenant ID: " + tenantId);
        }
    }

    /**
     * Verifies if a tenant exists by its tenant ID.
     *
     * <p>This private method is used by the controller methods to check if the given tenant ID exists
     * in the system. If the tenant is not found, a {@link HttpResouceNotFoundException} is thrown.</p>
     *
     * @param tenantId the ID of the tenant
     * @throws HttpResouceNotFoundException if the tenant is not found
     */
    protected void verifyResouce(String tenantId) throws HttpResouceNotFoundException {
        long count = pickListServive.countWhere("tenantId = '"+tenantId+"'");
        if (count <= 0) {
            throw new HttpResouceNotFoundException("tenant-id-tenantId-not-found", "Tenant ID #'"+tenantId+"' not found");
        }
    }
    
    /**
     * Verifies the existence of a pick list resource based on a given criteria string.
     *
     * <p>This protected method checks whether any pick lists exist that match the specified criteria.
     * It uses the {@code pickListService.countWhere(criteria)} method to count the number of pick lists
     * that satisfy the provided criteria. If no matching pick lists are found (i.e., the count is zero or less),
     * it throws a {@link HttpResouceNotFoundException} to indicate that no resources were found corresponding
     * to the criteria.</p>
     *
     * <p>The criteria string should be a valid query condition that can be understood by the underlying data access layer.
     * It might include conditions on tenant IDs, pick list statuses, or other relevant fields.</p>
     *
     * @param criteria the criteria string used to filter pick lists; must not be {@code null} or empty
     * @throws HttpResouceNotFoundException if no pick lists are found matching the provided criteria
     */
    protected void verifyResouceByCriteria(String criteria) throws HttpResouceNotFoundException {
        long count = pickListServive.countWhere(criteria);
        if (count <= 0) {
            throw new HttpResouceNotFoundException("no-picki-ist-found-correpondig-to-the-critiria","No PickList found correpondig to the critiria "+ criteria);
        }
    }
}

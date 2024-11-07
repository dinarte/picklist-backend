package com.quebecteh.modules.inventary.picklist.service;

import org.springframework.stereotype.Service;

import com.quebecteh.commons.multitenancy.AbstractMultiTenancyCrudService;
import com.quebecteh.modules.inventary.picklist.model.domain.PickListItem;

@Service
public class PickListItemServiceImpl extends AbstractMultiTenancyCrudService<PickListItem, Long> 
                                 implements PickListItemService {

}

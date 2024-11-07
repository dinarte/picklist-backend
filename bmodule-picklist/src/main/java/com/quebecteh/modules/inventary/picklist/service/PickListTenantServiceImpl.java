package com.quebecteh.modules.inventary.picklist.service;

import org.springframework.stereotype.Service;

import com.quebecteh.commons.multitenancy.AbstractMultiTenancyCrudService;
import com.quebecteh.modules.inventary.picklist.model.domain.PickListTenant;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PickListTenantServiceImpl extends AbstractMultiTenancyCrudService<PickListTenant, Long> 
                                 implements PickListTenantService {

}

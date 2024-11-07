package com.quebecteh.modules.commons.connector.repository;
import org.springframework.stereotype.Repository;

import com.quebecteh.commons.multitenancy.MultiTenancyCrudRepository;
import com.quebecteh.modules.commons.connector.model.domain.ZohoOrganization;

@Repository
public interface ZohoOrganizationRepository extends MultiTenancyCrudRepository<ZohoOrganization, Long> {
    
}

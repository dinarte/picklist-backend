package com.quebecteh.modules.commons.connector.repository;
import org.springframework.stereotype.Repository;

import com.quebecteh.commons.multitenancy.MultiTenancyCrudRepository;
import com.quebecteh.modules.commons.connector.model.domain.ZohoConnection;

@Repository
public interface ZohoConnectionRepository extends MultiTenancyCrudRepository<ZohoConnection, Long> {
    
}

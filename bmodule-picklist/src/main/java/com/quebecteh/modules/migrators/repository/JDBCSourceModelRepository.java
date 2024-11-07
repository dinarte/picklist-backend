package com.quebecteh.modules.migrators.repository;

import com.quebecteh.commons.multitenancy.MultiTenancyCrudRepository;
import com.quebecteh.modules.migrators.domain.JDBCSourceModel;

public interface JDBCSourceModelRepository extends MultiTenancyCrudRepository<JDBCSourceModel, Long> {

}

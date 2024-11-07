package com.quebecteh.modules.migrators.repository;

import com.quebecteh.commons.multitenancy.MultiTenancyCrudRepository;
import com.quebecteh.modules.migrators.domain.JSonSourceModel;

public interface JSonSourceModelRepository extends MultiTenancyCrudRepository<JSonSourceModel, Long> {

}

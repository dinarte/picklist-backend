package com.quebecteh.modules.migrators.service;

import org.springframework.stereotype.Service;

import com.quebecteh.commons.multitenancy.AbstractMultiTenancyCrudService;
import com.quebecteh.modules.migrators.domain.JDBCSourceModel;

@Service
public class JDBCSourceModelServiceImpl extends AbstractMultiTenancyCrudService<JDBCSourceModel, Long> implements JDBCSourceModelService  {

}

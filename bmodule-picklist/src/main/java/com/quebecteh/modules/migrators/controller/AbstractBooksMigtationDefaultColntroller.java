package com.quebecteh.modules.migrators.controller;

import com.quebecteh.modules.commons.clients.api.zoho.connector.ZohoConnectorProperties;
import com.quebecteh.modules.migrators.repository.MigrationLogRepository;
import com.quebecteh.modules.migrators.service.JDBCSourceConnectionService;

import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class AbstractBooksMigtationDefaultColntroller extends AbstractZohoMigration {

	public AbstractBooksMigtationDefaultColntroller(ZohoConnectorProperties connProperties,
			JDBCSourceConnectionService connectionService, MigrationLogRepository migrationLogRepository) {
		super(connProperties, connectionService, migrationLogRepository);
	}

	protected String getApiBaseUrl() {
		return connProperties.getApiBaseUrlBooks();
	}

}
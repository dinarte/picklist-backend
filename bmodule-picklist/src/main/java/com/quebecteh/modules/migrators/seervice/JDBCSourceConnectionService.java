package com.quebecteh.modules.migrators.seervice;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class JDBCSourceConnectionService {
	
	private final JDBCSourceDataSourceService dataSourceService;
	
	@Getter
	private Connection connection;

	@PostConstruct
	private void createConnection() throws SQLException{
		log.info("Creating connection");
		this.connection = dataSourceService.getDataSource().getConnection(); 
	}
	
	
	@PreDestroy
	private void closeConnection() throws SQLException {
		log.info("Colsing connection.");
		connection.close();
	}
	

}

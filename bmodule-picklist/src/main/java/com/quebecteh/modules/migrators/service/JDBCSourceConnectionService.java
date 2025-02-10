package com.quebecteh.modules.migrators.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	
	public ResultSet query(String query) throws SQLException {
		log.info("Executing query: {}", query);
		PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet;
	}
	
	public List<Map<String, Object>> queryToMap(String query) throws SQLException {
		log.info("Executing queryToMap");
       
        ResultSet resultSet = query(query);
            
            List<Map<String, Object>> results = new ArrayList<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object columnValue = resultSet.getObject(i);
                    row.put(columnName, columnValue != null ? columnValue : "");
                }
                results.add(row);
            }
            
            return results;
    }

}

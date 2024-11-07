package com.quebecteh.modules.migrators.seervice;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;

@Service
public class JDBCSourceDataSourceService {

    final String JDBC_URL_CONNECTION = "jdbc:mysql://localhost:3306/conscia1_bave?user=root&password=root&serverTimezone=UTC";

    @Getter
    private DriverManagerDataSource dataSource;

    @PostConstruct
    private DataSource createDataSource() {
        dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(JDBC_URL_CONNECTION);
        return dataSource;
    }
}
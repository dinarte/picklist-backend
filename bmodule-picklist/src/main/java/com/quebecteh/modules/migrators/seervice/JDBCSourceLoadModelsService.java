package com.quebecteh.modules.migrators.seervice;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.quebecteh.modules.migrators.domain.JDBCSourceModel;
import com.quebecteh.modules.migrators.domain.JDBCSourceModelField;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JDBCSourceLoadModelsService {

	private final JDBCSourceConnectionService connectionService;

    public List<JDBCSourceModel> loadModels(String tenantId, String appId) {
        List<JDBCSourceModel> models = new ArrayList<>();
        
        
        try (Connection connection = connectionService.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            // Obtenção de tabelas
            try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    
                    var foreignKeys = loadForeignKeys(metaData, tableName);

                    JDBCSourceModel model = new JDBCSourceModel();
                    model.setTenantId(tenantId);
                    model.setAppId(appId);
                    model.setName(tableName);
                    model.setScriptSql("SELECT * FROM " + tableName);
                    model.setPkFieldName(getPrimaryKeyField(metaData, tableName));
                    model.setFields(loadFields(tenantId, metaData, tableName, foreignKeys));

                    models.add(model);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return models;
    }

    private String getPrimaryKeyField(DatabaseMetaData metaData, String tableName) throws SQLException {
        try (ResultSet pkResultSet = metaData.getPrimaryKeys(null, null, tableName)) {
            if (pkResultSet.next()) {
                return pkResultSet.getString("COLUMN_NAME");
            }
        }
        return null;
    }

    private List<JDBCSourceModelField> loadFields(String tentantId, DatabaseMetaData metaData, String tableName, Map<String, String> foreignKeys) throws SQLException {
        List<JDBCSourceModelField> fields = new ArrayList<>();

        try (ResultSet columns = metaData.getColumns(null, null, tableName, "%")) {
            while (columns.next()) {
                JDBCSourceModelField field = new JDBCSourceModelField();
                field.setName(columns.getString("COLUMN_NAME"));
                field.setDataType(mapDataType(columns.getInt("DATA_TYPE")));
                field.setLength(columns.getInt("COLUMN_SIZE"));
                field.setFkModelName(foreignKeys.get(field.getName()));
                field.setTenantId(tentantId);
                fields.add(field);
            }
        }

        return fields;
    }
    
    
    private Map<String, String>  loadForeignKeys(DatabaseMetaData metaData, String tableName) throws SQLException {
    	
    	Map<String, String> foreignKeys = new HashMap<>();
        try (ResultSet foreignKeyColumns = metaData.getImportedKeys(null, null, tableName)) {
            while (foreignKeyColumns.next()) {
                String fkColumnName = foreignKeyColumns.getString("FKCOLUMN_NAME");
                String fkTableName = foreignKeyColumns.getString("PKTABLE_NAME");
                
        
                foreignKeys.put(fkColumnName, fkTableName);
            }
        }
        return foreignKeys;
    }
    

    private String mapDataType(int jdbcType) {
        switch (jdbcType) {
            case Types.INTEGER:
            case Types.BIGINT:
            case Types.SMALLINT:
            case Types.NUMERIC:
                return "number";
            case Types.VARCHAR:
            case Types.CHAR:
            case Types.LONGVARCHAR:
                return "text";
            case Types.DATE:
                return "date";
            case Types.TIME:
                return "time";
            case Types.TIMESTAMP:
                return "dateTime";
            default:
                return "texto"; // Tipo padrão
        }
    }

}

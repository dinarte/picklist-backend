package com.quebecteh.modules.migrators.domain;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="jdbc_source_model", schema = "migrations")
@Data
public class JDBCSourceModel implements SourceModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String appId;
    private String name;
    private String scriptSql;
    private String pkFieldName;
    private String tenantId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<JDBCSourceModelField> fields;

    @ElementCollection
    private List<String> relatedModelsList;

} 
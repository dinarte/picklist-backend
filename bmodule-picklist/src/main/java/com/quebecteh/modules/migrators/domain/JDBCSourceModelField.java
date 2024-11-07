package com.quebecteh.modules.migrators.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="jdbc_source_model_field", schema = "migrations")
public class JDBCSourceModelField{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	@ManyToOne
	private JDBCSourceModel model;
    private String name;
    private String dataType;
    private Integer length;
    private String fkModelName;
    private Boolean required;
    private String description;
    private String tenantId;
}
package com.quebecteh.modules.migrators.domain;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="json_source_model_field", schema = "migrations")
public class JSonSourceModelField {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne
	private JSonSourceModel model;
    private String name;
    private String type;
    private Integer length;
    private Boolean required;
    private String description;
    private String tenantId; 
    
 
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private JSonSourceModelField parent;

 
    @OneToMany(mappedBy = "parent")
    private List<JSonSourceModelField> fields;
}
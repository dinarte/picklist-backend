package com.quebecteh.modules.migrators.domain;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="migration_log", schema = "migrations")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MigrationLog {
	
	public static final String ERROR = "err";
	public static final String SUCCESS = "success";
	public static final String DELETE_ERROR = "delete err";
	public static final String DELETED = "deleted";
	public static final String UPDATE_ERROR = "update err";
	public static final String UPDATED = "updated";
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	String sourceAppName;
	String sourceEntity;
	String sourceId;
	String destinationApp;
	String destinationEntity;
	String destinationId;
	String status;
	String message;
	
	@OneToOne(mappedBy = "migrationLog", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
	DestinationData destinationData;
	
	LocalDateTime dateTime;
	String tenantId;

}

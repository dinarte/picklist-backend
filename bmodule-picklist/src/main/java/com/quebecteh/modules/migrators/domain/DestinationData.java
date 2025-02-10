package com.quebecteh.modules.migrators.domain;

import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="destination_data", schema = "migrations")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DestinationData {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	@OneToOne
	@JoinColumn(name = "migration_log_id", referencedColumnName = "id")
	MigrationLog migrationLog;
	
	//@Column(columnDefinition = "jsonb")
	@JdbcTypeCode(SqlTypes.JSON)
	Map<String, Object> data;

	String tenantId;

}

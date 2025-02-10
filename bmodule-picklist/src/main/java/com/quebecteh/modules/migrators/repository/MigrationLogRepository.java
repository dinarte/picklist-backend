package com.quebecteh.modules.migrators.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.quebecteh.commons.multitenancy.MultiTenancyCrudRepository;
import com.quebecteh.modules.migrators.domain.MigrationLog;

public interface MigrationLogRepository extends MultiTenancyCrudRepository<MigrationLog, Long> {
	
	
	 @Query("SELECT m.sourceId FROM MigrationLog m " +
	           "WHERE m.tenantId = :tenantId " +
	           "AND m.sourceAppName = :sourceAppName " +
	           "AND m.sourceEntity = :sourceEntity " +
	           "AND m.destinationApp = :destinationApp " +
	           "AND m.destinationEntity = :destinationEntity " +
	           "AND m.status = :status")
    List<String> findSourceIdsByFilters(
            @Param("tenantId") String tenantId,
            @Param("sourceAppName") String sourceAppName,
            @Param("sourceEntity") String sourceEntity,
            @Param("destinationApp") String destinationApp,
            @Param("destinationEntity") String destinationEntity,
            @Param("status") String status);
	 
	 
	 @Query("SELECT m.destinationId FROM MigrationLog m " +
	           "WHERE m.tenantId = :tenantId " +
	           "AND m.sourceAppName = :sourceAppName " +
	           "AND m.sourceEntity = :sourceEntity " +
	           "AND m.sourceId = :sourceId " +
	           "AND m.destinationApp = :destinationApp " +
	           "AND m.destinationEntity = :destinationEntity " +
	           "AND m.status = :status")
	 String getDestinationIdByFilters(
          @Param("tenantId") String tenantId,
          @Param("sourceAppName") String sourceAppName,
          @Param("sourceEntity") String sourceEntity,
          @Param("sourceId") String sourceId,
          @Param("destinationApp") String destinationApp,
          @Param("destinationEntity") String destinationEntity,
          @Param("status") String status);
	 
	 
	 @Query("SELECT m.destinationId FROM MigrationLog m " +
	           "WHERE m.tenantId = :tenantId " +
	           "AND m.destinationApp = :destinationApp " +
	           "AND m.destinationEntity = :destinationEntity " +
	           "AND m.status = :status " +
	           "ORDER BY m.id")
	 List<String> findDestinationIdsByFilters(
        @Param("tenantId") String tenantId,
        @Param("destinationApp") String destinationApp,
        @Param("destinationEntity") String destinationEntity,
        @Param("status") String status);
	 
	 
	 @Query("FROM MigrationLog m " +
	           "WHERE m.tenantId = :tenantId " +
	           "AND m.destinationApp = :destinationApp " +
	           "AND m.destinationEntity = :destinationEntity " +
	           "AND m.status = :status " +
	           "ORDER BY m.id")
	 List<MigrationLog> findAllByFilters(
      @Param("tenantId") String tenantId,
      @Param("destinationApp") String destinationApp,
      @Param("destinationEntity") String destinationEntity,
      @Param("status") String status);
	 
	 @Query("FROM MigrationLog m " +
	           "WHERE m.tenantId = :tenantId " +
	           "AND m.destinationApp = :destinationApp " +
	           "AND m.destinationEntity = :destinationEntity " +
	           "AND m.status = :status " +
	           "AND m.sourceId in :sourceIdList " +
	           "ORDER BY m.id")
	 List<MigrationLog> findAllByFiltersAndSourceIds(
    @Param("tenantId") String tenantId,
    @Param("destinationApp") String destinationApp,
    @Param("destinationEntity") String destinationEntity,
    @Param("status") String status,
    @Param("sourceIdList") List<String> sourceIdList);

	 
	 @Query("SELECT m FROM MigrationLog m " +
	           "WHERE m.tenantId = :tenantId " +
	           "AND m.sourceAppName = :sourceAppName " +
	           "AND m.sourceEntity = :sourceEntity " +
	           "AND m.sourceId = :sourceId " +
	           "AND m.destinationApp = :destinationApp " +
	           "AND m.destinationEntity = :destinationEntity " +
	           "AND m.status = :status")
	 Optional<MigrationLog> findOneByFilters(
        @Param("tenantId") String tenantId,
        @Param("sourceAppName") String sourceAppName,
        @Param("sourceEntity") String sourceEntity,
        @Param("sourceId") String sourceId,
        @Param("destinationApp") String destinationApp,
        @Param("destinationEntity") String destinationEntity,
        @Param("status") String status);	 
}

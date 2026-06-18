package com.trevix.property_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.trevix.property_management.entity.RFIDLog;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface RFIDLogRepository extends JpaRepository<RFIDLog, UUID> {
    
    List<RFIDLog> findByPropertyId(UUID propertyId);
    
    List<RFIDLog> findByTenant_UserId(UUID tenantId);
    
    @Query("SELECT r FROM RFIDLog r WHERE r.property.id = :propertyId AND r.scanTime BETWEEN :startDate AND :endDate")
    List<RFIDLog> findByPropertyAndDateRange(@Param("propertyId") UUID propertyId, 
                                              @Param("startDate") OffsetDateTime startDate, 
                                              @Param("endDate") OffsetDateTime endDate);
    
    @Query("SELECT COUNT(r) FROM RFIDLog r WHERE r.property.id = :propertyId AND r.direction = 'ENTRY' AND r.scanTime BETWEEN :startDate AND :endDate")
    long countEntriesByPropertyAndDateRange(@Param("propertyId") UUID propertyId, 
                                             @Param("startDate") OffsetDateTime startDate, 
                                             @Param("endDate") OffsetDateTime endDate);
    
    @Query("SELECT r FROM RFIDLog r WHERE r.isSuspicious = true")
    List<RFIDLog> findSuspiciousLogs();
}
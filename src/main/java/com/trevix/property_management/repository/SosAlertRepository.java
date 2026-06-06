package com.trevix.property_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.trevix.property_management.entity.SOSAlert;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SosAlertRepository extends JpaRepository<SOSAlert, UUID> {
    
    List<SOSAlert> findByPropertyId(UUID propertyId);
    
    List<SOSAlert> findByTenant_UserId(UUID tenantId);
    
    List<SOSAlert> findByStatus(String status);
    
    @Query("SELECT s FROM SOSAlert s WHERE s.property.id = :propertyId AND s.status = 'pending'")
    List<SOSAlert> findPendingByProperty(@Param("propertyId") UUID propertyId);
    
    @Modifying
    @Transactional
    @Query("UPDATE SOSAlert s SET s.status = :status, s.resolvedTime = :resolvedTime, s.respondedBy = :respondedById WHERE s.id = :alertId")
    void resolveAlert(@Param("alertId") UUID alertId, 
                      @Param("status") String status, 
                      @Param("resolvedTime") OffsetDateTime resolvedTime,
                      @Param("respondedById") UUID respondedById);
}
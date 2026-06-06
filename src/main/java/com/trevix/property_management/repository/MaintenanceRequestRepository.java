package com.trevix.property_management.repository;

import com.trevix.property_management.entity.MaintenanceRequest;
import com.trevix.property_management.enums.MaintenanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, UUID> {
    
    List<MaintenanceRequest> findByTenant_UserId(UUID tenantId);
    
    List<MaintenanceRequest> findByPropertyId(UUID propertyId);
    
    List<MaintenanceRequest> findByAssignedTo_UserId(UUID staffUserId);
    
    List<MaintenanceRequest> findByStatus(MaintenanceStatus status);
    
    @Query("SELECT m FROM MaintenanceRequest m WHERE m.property.id = :propertyId AND m.status = 'PENDING'")
    List<MaintenanceRequest> findPendingByProperty(@Param("propertyId") UUID propertyId);
    
    @Query("SELECT m FROM MaintenanceRequest m WHERE m.assignedTo.userId = :staffId AND m.status IN ('ASSIGNED', 'IN_PROGRESS')")
    List<MaintenanceRequest> findActiveByStaff(@Param("staffId") UUID staffId);
    
    @Query("SELECT COUNT(m) FROM MaintenanceRequest m WHERE m.property.id = :propertyId AND m.status = 'PENDING'")
    long countPendingByProperty(@Param("propertyId") UUID propertyId);
    
    @Query("SELECT AVG(EXTRACT(EPOCH FROM (m.completedAt - m.createdAt)) / 3600) FROM MaintenanceRequest m WHERE m.status = 'COMPLETED' AND m.property.id = :propertyId")
    Double getAverageCompletionHours(@Param("propertyId") UUID propertyId);
    
    @Modifying
    @Transactional
    @Query("UPDATE MaintenanceRequest m SET m.status = :status, m.assignedTo.userId = :staffId WHERE m.id = :requestId")
    void assignToStaff(@Param("requestId") UUID requestId, 
                       @Param("staffId") UUID staffId, 
                       @Param("status") MaintenanceStatus status);
    
    @Modifying
    @Transactional
    @Query("UPDATE MaintenanceRequest m SET m.status = :status, m.completedAt = :completedAt WHERE m.id = :requestId")
    void completeRequest(@Param("requestId") UUID requestId, 
                         @Param("status") MaintenanceStatus status, 
                         @Param("completedAt") OffsetDateTime completedAt);
}
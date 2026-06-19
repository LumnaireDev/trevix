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

    List<MaintenanceRequest> findByProperty_Id(UUID propertyId);

    List<MaintenanceRequest> findByTenant_UserId(UUID tenantId);

    List<MaintenanceRequest> findByStatus(MaintenanceStatus status);

    @Query("SELECT m FROM MaintenanceRequest m WHERE m.property.id = :propertyId AND m.status = :status ORDER BY m.createdAt DESC")
    List<MaintenanceRequest> findByPropertyAndStatus(@Param("propertyId") UUID propertyId,
                                                     @Param("status") MaintenanceStatus status);

    @Query("SELECT m FROM MaintenanceRequest m WHERE m.property.owner.id = :ownerId ORDER BY m.createdAt DESC")
    List<MaintenanceRequest> findByOwnerId(@Param("ownerId") UUID ownerId);

    @Query("SELECT m FROM MaintenanceRequest m WHERE m.property.owner.id = :ownerId ORDER BY m.createdAt DESC")
    Page<MaintenanceRequest> findRecentByOwner(@Param("ownerId") UUID ownerId, Pageable pageable);

    @Query("SELECT COUNT(m) FROM MaintenanceRequest m WHERE m.property.owner.id = :ownerId AND m.status = :status")
    long countByOwnerAndStatus(@Param("ownerId") UUID ownerId, @Param("status") MaintenanceStatus status);

    @Query("SELECT COUNT(m) FROM MaintenanceRequest m WHERE m.property.owner.id = :ownerId AND m.priority = :priority")
    long countByOwnerAndPriority(@Param("ownerId") UUID ownerId, @Param("priority") String priority);

    @Modifying
    @Transactional
    @Query("UPDATE MaintenanceRequest m SET m.status = :status, m.completedAt = :completedAt WHERE m.id = :id")
    void updateStatus(@Param("id") UUID id,
                      @Param("status") MaintenanceStatus status,
                      @Param("completedAt") OffsetDateTime completedAt);
}
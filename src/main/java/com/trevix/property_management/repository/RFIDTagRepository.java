package com.trevix.property_management.repository;

import com.trevix.property_management.entity.RFIDTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RFIDTagRepository extends JpaRepository<RFIDTag, UUID> {
    
    Optional<RFIDTag> findByTagUid(String tagUid);
    
    List<RFIDTag> findByTenant_UserId(UUID tenantId);
    
    List<RFIDTag> findByIsActiveTrue();
    
    @Query("SELECT r FROM RFIDTag r WHERE r.tenant.userId = :tenantId AND r.isActive = true")
    Optional<RFIDTag> findActiveByTenant(@Param("tenantId") UUID tenantId);
    
    @Modifying
    @Transactional
    @Query("UPDATE RFIDTag r SET r.isActive = false WHERE r.tenant.userId = :tenantId")
    void deactivateAllByTenant(@Param("tenantId") UUID tenantId);
}
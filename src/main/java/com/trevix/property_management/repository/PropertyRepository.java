package com.trevix.property_management.repository;

import com.trevix.property_management.entity.Property;
import com.trevix.property_management.enums.PropertyStatus;
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
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PropertyRepository extends JpaRepository<Property, UUID> {
    
    List<Property> findByAdmin_UserId(UUID adminId);
    
    Page<Property> findByAdmin_UserId(UUID adminId, Pageable pageable);
    
    List<Property> findByStatus(PropertyStatus status);
    
    @Query("SELECT p FROM Property p WHERE p.admin.userId = :adminId AND p.deletedAt IS NULL")
    List<Property> findActiveByAdmin(@Param("adminId") UUID adminId);
    
    @Query("SELECT COUNT(p) FROM Property p WHERE p.admin.userId = :adminId AND p.deletedAt IS NULL")
    long countActiveByAdmin(@Param("adminId") UUID adminId);
    
    @Query("SELECT p FROM Property p WHERE p.status = 'ACTIVE'")
    List<Property> findAllActive();
    
    @Query("SELECT p FROM Property p JOIN p.rooms r JOIN r.leases l WHERE l.tenant.userId = :tenantId AND l.status = 'ACTIVE'")
    Optional<Property> findPropertyByTenantId(@Param("tenantId") UUID tenantId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Property p SET p.deletedAt = :deletedAt WHERE p.id = :propertyId")
    void softDelete(@Param("propertyId") UUID propertyId, @Param("deletedAt") OffsetDateTime deletedAt);
}
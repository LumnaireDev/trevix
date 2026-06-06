package com.trevix.property_management.repository;

import com.trevix.property_management.entity.Package;
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
public interface PackageRepository extends JpaRepository<Package, UUID> {
    
    List<Package> findByPropertyId(UUID propertyId);
    
    List<Package> findByTenant_UserId(UUID tenantId);
    
    @Query("SELECT p FROM Package p WHERE p.tenant.userId = :tenantId AND p.pickedUpAt IS NULL")
    List<Package> findUnpickedPackagesByTenant(@Param("tenantId") UUID tenantId);
    
    @Query("SELECT p FROM Package p WHERE p.property.id = :propertyId AND p.pickedUpAt IS NULL")
    List<Package> findUnpickedPackagesByProperty(@Param("propertyId") UUID propertyId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Package p SET p.pickedUpBy = :tenantId, p.pickedUpAt = :pickupTime WHERE p.id = :packageId")
    void pickUpPackage(@Param("packageId") UUID packageId, 
                       @Param("tenantId") UUID tenantId, 
                       @Param("pickupTime") OffsetDateTime pickupTime);
    
    @Modifying
    @Transactional
    @Query("UPDATE Package p SET p.notified = true WHERE p.id = :packageId")
    void markNotified(@Param("packageId") UUID packageId);
}
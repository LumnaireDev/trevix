package com.trevix.property_management.repository;

import com.trevix.property_management.entity.PropertyModule;
import com.trevix.property_management.enums.ModuleType;
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
public interface PropertyModuleRepository extends JpaRepository<PropertyModule, UUID> {
    
    List<PropertyModule> findByPropertyId(UUID propertyId);
    
    List<PropertyModule> findByIsActiveTrue();
    
    Optional<PropertyModule> findByPropertyIdAndModuleType(UUID propertyId, ModuleType moduleType);
    
    @Query("SELECT pm FROM PropertyModule pm WHERE pm.property.id = :propertyId AND pm.isActive = true")
    List<PropertyModule> findActiveByProperty(@Param("propertyId") UUID propertyId);
    
    @Query("SELECT pm FROM PropertyModule pm WHERE pm.property.id = :propertyId AND pm.moduleType = :moduleType AND pm.isActive = true")
    Optional<PropertyModule> findActiveByPropertyAndModule(@Param("propertyId") UUID propertyId, @Param("moduleType") ModuleType moduleType);
    
    @Modifying
    @Transactional
    @Query("UPDATE PropertyModule pm SET pm.isActive = true, pm.activatedAt = :activatedAt WHERE pm.id = :moduleId")
    void activateModule(@Param("moduleId") UUID moduleId, @Param("activatedAt") OffsetDateTime activatedAt);
    
    @Modifying
    @Transactional
    @Query("UPDATE PropertyModule pm SET pm.isActive = false WHERE pm.property.id = :propertyId AND pm.moduleType = :moduleType")
    void deactivateModule(@Param("propertyId") UUID propertyId, @Param("moduleType") ModuleType moduleType);
}
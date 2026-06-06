package com.trevix.property_management.repository;

import com.trevix.property_management.entity.StaffProfile;
import com.trevix.property_management.enums.StaffType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffProfileRepository extends JpaRepository<StaffProfile, UUID> {
    
    Optional<StaffProfile> findByUserId(UUID userId);
    
    List<StaffProfile> findByStaffType(StaffType staffType);
    
    List<StaffProfile> findByIsActiveTrue();
    
    @Query("SELECT s FROM StaffProfile s WHERE s.isActive = true AND s.staffType = :staffType")
    List<StaffProfile> findActiveByStaffType(@Param("staffType") StaffType staffType);
    
    @Query("SELECT s FROM StaffProfile s JOIN s.propertyAssignments pa WHERE pa.property.id = :propertyId AND s.isActive = true")
    List<StaffProfile> findByPropertyId(@Param("propertyId") UUID propertyId);
    
    @Query("SELECT s FROM StaffProfile s JOIN s.propertyAssignments pa WHERE pa.property.id = :propertyId AND s.staffType = :staffType")
    List<StaffProfile> findByPropertyAndStaffType(@Param("propertyId") UUID propertyId, @Param("staffType") StaffType staffType);
    
    @Query("SELECT COUNT(s) FROM StaffProfile s WHERE s.staffType = :staffType AND s.isActive = true")
    long countActiveByStaffType(@Param("staffType") StaffType staffType);
}
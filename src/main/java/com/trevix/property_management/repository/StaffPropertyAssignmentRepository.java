package com.trevix.property_management.repository;

import com.trevix.property_management.entity.StaffPropertyAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Repository
public interface StaffPropertyAssignmentRepository extends JpaRepository<StaffPropertyAssignment, UUID> {
    
    List<StaffPropertyAssignment> findByStaffUser_UserId(UUID staffUserId);
    
    List<StaffPropertyAssignment> findByPropertyId(UUID propertyId);
    
    @Query("SELECT spa FROM StaffPropertyAssignment spa WHERE spa.staffUser.userId = :staffUserId AND spa.property.id = :propertyId")
    List<StaffPropertyAssignment> findByStaffAndProperty(@Param("staffUserId") UUID staffUserId, @Param("propertyId") UUID propertyId);
    
    boolean existsByStaffUser_UserIdAndPropertyId(UUID staffUserId, UUID propertyId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM StaffPropertyAssignment spa WHERE spa.staffUser.userId = :staffUserId AND spa.property.id = :propertyId")
    void deleteByStaffAndProperty(@Param("staffUserId") UUID staffUserId, @Param("propertyId") UUID propertyId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM StaffPropertyAssignment spa WHERE spa.staffUser.userId = :staffUserId")
    void deleteAllByStaff(@Param("staffUserId") UUID staffUserId);
}
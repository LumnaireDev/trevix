package com.trevix.property_management.repository;

import com.trevix.property_management.entity.MaintenanceRequestPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Repository
public interface MaintenanceRequestPhotoRepository extends JpaRepository<MaintenanceRequestPhoto, UUID> {
    
    List<MaintenanceRequestPhoto> findByRequestId(UUID requestId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM MaintenanceRequestPhoto mrp WHERE mrp.request.id = :requestId")
    void deleteByRequestId(@Param("requestId") UUID requestId);
}
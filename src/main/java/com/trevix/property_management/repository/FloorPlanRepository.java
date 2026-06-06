package com.trevix.property_management.repository;

import com.trevix.property_management.entity.FloorPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface FloorPlanRepository extends JpaRepository<FloorPlan, UUID> {
    
    List<FloorPlan> findByPropertyId(UUID propertyId);
    
    void deleteByPropertyId(UUID propertyId);
}
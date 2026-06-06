package com.trevix.property_management.repository.custom;

import com.trevix.property_management.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public interface CustomTenantRepository {
    
    Page<Tenant> findTenantsWithFilters(UUID propertyId, String searchTerm, Boolean isActive, Pageable pageable);
    
    Map<String, Object> getTenantDashboardStats(UUID propertyId);
    
    void updateScorecardScores(UUID propertyId);
}
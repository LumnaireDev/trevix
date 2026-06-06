package com.trevix.property_management.repository;

import com.trevix.property_management.entity.IncidentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface IncidentReportRepository extends JpaRepository<IncidentReport, UUID> {
    
    List<IncidentReport> findByPropertyId(UUID propertyId);
    
    List<IncidentReport> findByReportedBy_UserId(UUID userId);
    
    @Query("SELECT i FROM IncidentReport i WHERE i.property.id = :propertyId AND i.incidentTime BETWEEN :startDate AND :endDate")
    List<IncidentReport> findByPropertyAndDateRange(@Param("propertyId") UUID propertyId, 
                                                     @Param("startDate") OffsetDateTime startDate, 
                                                     @Param("endDate") OffsetDateTime endDate);
    
    @Query("SELECT i FROM IncidentReport i WHERE i.severity = :severity")
    List<IncidentReport> findBySeverity(@Param("severity") String severity);
}
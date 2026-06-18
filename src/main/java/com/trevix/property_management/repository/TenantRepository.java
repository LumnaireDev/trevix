package com.trevix.property_management.repository;

import com.trevix.property_management.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    
    Optional<Tenant> findByUserId(UUID userId);
    
    @Query("SELECT t FROM Tenant t WHERE t.isActive = true")
    List<Tenant> findAllActive();
    
    @Query("SELECT DISTINCT t FROM Tenant t JOIN Lease l ON l.tenant = t " +
           "WHERE t.isActive = true AND l.property.id = :propertyId AND l.status = 'ACTIVE'")
    List<Tenant> findByPropertyId(@Param("propertyId") UUID propertyId);

    @Query("SELECT DISTINCT t FROM Tenant t JOIN Lease l ON l.tenant = t " +
           "WHERE t.isActive = true AND l.property.id = :propertyId AND l.room.id = :roomId AND l.status = 'ACTIVE'")
    List<Tenant> findByPropertyAndRoom(@Param("propertyId") UUID propertyId, @Param("roomId") UUID roomId);

    @Query("SELECT COUNT(DISTINCT t) FROM Tenant t JOIN Lease l ON l.tenant = t " +
           "WHERE t.isActive = true AND l.property.id = :propertyId AND l.status = 'ACTIVE'")
    long countActiveByProperty(@Param("propertyId") UUID propertyId);

    @Query("SELECT t FROM Tenant t WHERE t.scorecardScore < :threshold ORDER BY t.scorecardScore ASC")
    List<Tenant> findLowScorecardTenants(@Param("threshold") BigDecimal threshold);

    @Query("SELECT AVG(t.scorecardScore) FROM Tenant t JOIN Lease l ON l.tenant = t " +
           "WHERE l.property.id = :propertyId AND l.status = 'ACTIVE'")
    BigDecimal getAverageScorecardScore(@Param("propertyId") UUID propertyId);
    
    @Query("SELECT t FROM Tenant t WHERE t.moveInDate BETWEEN :startDate AND :endDate")
    List<Tenant> findTenantsByMoveInDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Modifying
    @Transactional
    @Query("UPDATE Tenant t SET t.isActive = false WHERE t.userId = :tenantId")
    void deactivateTenant(@Param("tenantId") UUID tenantId);

    @Query("SELECT COUNT(DISTINCT t) FROM Tenant t JOIN Lease l ON l.tenant = t " +
           "WHERE t.isActive = true AND l.status = 'ACTIVE' AND l.property.admin.userId = :adminId")
    long countActiveByAdmin(@Param("adminId") UUID adminId);
}
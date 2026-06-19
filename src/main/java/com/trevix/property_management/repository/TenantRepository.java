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

    // Tenants linked via MonthlyBill (Lease is deleted)
    @Query("SELECT DISTINCT b.tenant FROM MonthlyBill b WHERE b.tenant.isActive = true AND b.property.id = :propertyId")
    List<Tenant> findByPropertyId(@Param("propertyId") UUID propertyId);

    @Query("SELECT COUNT(DISTINCT b.tenant) FROM MonthlyBill b WHERE b.tenant.isActive = true AND b.property.id = :propertyId")
    long countActiveByProperty(@Param("propertyId") UUID propertyId);

    @Query("SELECT COUNT(DISTINCT b.tenant) FROM MonthlyBill b WHERE b.tenant.isActive = true AND b.property.owner.id = :ownerId")
    long countActiveByOwner(@Param("ownerId") UUID ownerId);

    @Query("SELECT t FROM Tenant t WHERE t.moveInDate BETWEEN :startDate AND :endDate")
    List<Tenant> findTenantsByMoveInDateRange(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    @Modifying
    @Transactional
    @Query("UPDATE Tenant t SET t.isActive = false WHERE t.userId = :tenantId")
    void deactivateTenant(@Param("tenantId") UUID tenantId);
}
package com.trevix.property_management.repository;

import com.trevix.property_management.entity.Lease;
import com.trevix.property_management.enums.LeaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaseRepository extends JpaRepository<Lease, UUID> {
    
    List<Lease> findByTenant_UserId(UUID tenantId);
    
    List<Lease> findByRoomId(UUID roomId);
    
    List<Lease> findByPropertyId(UUID propertyId);
    
    List<Lease> findByStatus(LeaseStatus status);
    
    @Query("SELECT l FROM Lease l WHERE l.tenant.userId = :tenantId AND l.status = 'ACTIVE'")
    Optional<Lease> findActiveLeaseByTenant(@Param("tenantId") UUID tenantId);
    
    @Query("SELECT l FROM Lease l WHERE l.room.id = :roomId AND l.status = 'ACTIVE'")
    Optional<Lease> findActiveLeaseByRoom(@Param("roomId") UUID roomId);
    
    @Query("SELECT l FROM Lease l WHERE l.endDate < :date AND l.status = 'ACTIVE'")
    List<Lease> findExpiredLeases(@Param("date") LocalDate date);
    
    @Query("SELECT l FROM Lease l WHERE l.endDate BETWEEN :startDate AND :endDate AND l.status = 'ACTIVE'")
    List<Lease> findLeasesExpiringSoon(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT l FROM Lease l WHERE l.startDate <= :date AND l.endDate >= :date AND l.status = 'ACTIVE'")
    List<Lease> findActiveLeasesAtDate(@Param("date") LocalDate date);
    
    @Modifying
    @Transactional
    @Query("UPDATE Lease l SET l.status = 'EXPIRED' WHERE l.endDate < :date AND l.status = 'ACTIVE'")
    void updateExpiredLeases(@Param("date") LocalDate date);
}
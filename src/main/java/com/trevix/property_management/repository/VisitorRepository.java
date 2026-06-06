package com.trevix.property_management.repository;

import com.trevix.property_management.entity.Visitor;
import com.trevix.property_management.enums.VisitorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, UUID> {
    
    List<Visitor> findByPropertyId(UUID propertyId);
    
    List<Visitor> findByTenant_UserId(UUID tenantId);
    
    List<Visitor> findByStatus(VisitorStatus status);
    
    @Query("SELECT v FROM Visitor v WHERE v.property.id = :propertyId AND v.status = 'PENDING'")
    List<Visitor> findPendingByProperty(@Param("propertyId") UUID propertyId);
    
    @Query("SELECT v FROM Visitor v WHERE v.property.id = :propertyId AND v.actualCheckin BETWEEN :startDate AND :endDate")
    List<Visitor> findByPropertyAndCheckinDateRange(@Param("propertyId") UUID propertyId, 
                                                      @Param("startDate") OffsetDateTime startDate, 
                                                      @Param("endDate") OffsetDateTime endDate);
    
    @Modifying
    @Transactional
    @Query("UPDATE Visitor v SET v.status = 'CHECKED_IN', v.actualCheckin = :checkinTime WHERE v.id = :visitorId")
    void checkIn(@Param("visitorId") UUID visitorId, @Param("checkinTime") OffsetDateTime checkinTime);
    
    @Modifying
    @Transactional
    @Query("UPDATE Visitor v SET v.status = 'CHECKED_OUT', v.actualCheckout = :checkoutTime WHERE v.id = :visitorId")
    void checkOut(@Param("visitorId") UUID visitorId, @Param("checkoutTime") OffsetDateTime checkoutTime);
}
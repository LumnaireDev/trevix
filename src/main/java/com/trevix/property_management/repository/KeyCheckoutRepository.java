package com.trevix.property_management.repository;

import com.trevix.property_management.entity.KeyCheckout;
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
public interface KeyCheckoutRepository extends JpaRepository<KeyCheckout, UUID> {
    
    List<KeyCheckout> findByPropertyId(UUID propertyId);
    
    List<KeyCheckout> findByCheckedOutTo_UserId(UUID tenantId);
    
    List<KeyCheckout> findByIsReturnedFalse();
    
    @Query("SELECT k FROM KeyCheckout k WHERE k.property.id = :propertyId AND k.isReturned = false")
    List<KeyCheckout> findActiveCheckoutsByProperty(@Param("propertyId") UUID propertyId);
    
    @Query("SELECT k FROM KeyCheckout k WHERE k.expectedReturnTime < :now AND k.isReturned = false")
    List<KeyCheckout> findOverdueKeys(@Param("now") OffsetDateTime now);
    
    @Modifying
    @Transactional
    @Query("UPDATE KeyCheckout k SET k.isReturned = true, k.returnTime = :returnTime WHERE k.id = :checkoutId")
    void returnKey(@Param("checkoutId") UUID checkoutId, @Param("returnTime") OffsetDateTime returnTime);
}
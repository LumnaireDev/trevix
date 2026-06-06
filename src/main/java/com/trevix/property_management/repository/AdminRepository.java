package com.trevix.property_management.repository;

import com.trevix.property_management.entity.Admin;
import com.trevix.property_management.enums.SubscriptionPlan;
import com.trevix.property_management.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminRepository extends JpaRepository<Admin, UUID> {
    
    Optional<Admin> findByUserId(UUID userId);
    
    List<Admin> findBySubscriptionPlan(SubscriptionPlan plan);
    
    List<Admin> findBySubscriptionStatus(SubscriptionStatus status);
    
    @Query("SELECT a FROM Admin a WHERE a.subscriptionStatus = 'TRIAL' AND a.trialEndDate < :now")
    List<Admin> findExpiredTrials(@Param("now") OffsetDateTime now);
    
    @Query("SELECT a FROM Admin a WHERE a.subscriptionStatus = 'ACTIVE' AND a.currentPeriodEnd < :now")
    List<Admin> findExpiredSubscriptions(@Param("now") OffsetDateTime now);
    
    @Query("SELECT COUNT(p) FROM Property p WHERE p.admin.userId = :adminId")
    long countPropertiesByAdmin(@Param("adminId") UUID adminId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Admin a SET a.subscriptionStatus = :status, a.currentPeriodEnd = :periodEnd WHERE a.userId = :adminId")
    void updateSubscriptionStatus(@Param("adminId") UUID adminId, 
                                   @Param("status") SubscriptionStatus status, 
                                   @Param("periodEnd") OffsetDateTime periodEnd);
    
    @Query("SELECT a FROM Admin a WHERE a.autoRenew = true AND a.subscriptionStatus = 'ACTIVE'")
    List<Admin> findAutoRenewSubscriptions();
}
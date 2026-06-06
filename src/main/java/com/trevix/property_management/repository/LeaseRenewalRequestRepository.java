package com.trevix.property_management.repository;

import com.trevix.property_management.entity.LeaseRenewalRequest;
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
public interface LeaseRenewalRequestRepository extends JpaRepository<LeaseRenewalRequest, UUID> {
    
    List<LeaseRenewalRequest> findByLeaseId(UUID leaseId);
    
    List<LeaseRenewalRequest> findByStatus(String status);
    
    @Query("SELECT lrr FROM LeaseRenewalRequest lrr WHERE lrr.lease.tenant.userId = :tenantId AND lrr.status = 'pending'")
    List<LeaseRenewalRequest> findPendingByTenant(@Param("tenantId") UUID tenantId);
    
    @Modifying
    @Transactional
    @Query("UPDATE LeaseRenewalRequest lrr SET lrr.status = :status, lrr.reviewedAt = :reviewedAt, lrr.reviewedBy = :reviewedById WHERE lrr.id = :requestId")
    void reviewRequest(@Param("requestId") UUID requestId, 
                       @Param("status") String status, 
                       @Param("reviewedAt") OffsetDateTime reviewedAt,
                       @Param("reviewedById") UUID reviewedById);
}
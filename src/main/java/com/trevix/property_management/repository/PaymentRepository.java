package com.trevix.property_management.repository;

import com.trevix.property_management.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByBill_Id(UUID billId);

    @Query("SELECT p FROM Payment p WHERE p.bill.tenant.userId = :tenantId ORDER BY p.createdAt DESC")
    List<Payment> findByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT p FROM Payment p WHERE p.bill.property.owner.id = :ownerId ORDER BY p.createdAt DESC")
    Page<Payment> findRecentByOwner(@Param("ownerId") UUID ownerId, Pageable pageable);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.bill.tenant.userId = :tenantId AND p.status = 'PAID'")
    BigDecimal getTotalPaidByTenant(@Param("tenantId") UUID tenantId);

    @Modifying
    @Transactional
    @Query("UPDATE Payment p SET p.status = :status, p.paidAt = :paidAt WHERE p.id = :paymentId")
    void updateStatus(@Param("paymentId") UUID paymentId,
                      @Param("status") String status,
                      @Param("paidAt") OffsetDateTime paidAt);
}
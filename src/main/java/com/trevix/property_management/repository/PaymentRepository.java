package com.trevix.property_management.repository;

import com.trevix.property_management.entity.Payment;
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
    
    List<Payment> findByBillId(UUID billId);
    
    List<Payment> findByStatus(String status);
    
    @Query("SELECT p FROM Payment p WHERE p.xenditChargeId = :chargeId")
    List<Payment> findByXenditChargeId(@Param("chargeId") String chargeId);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.bill.tenant.userId = :tenantId AND p.status = 'completed'")
    BigDecimal getTotalPaymentsByTenant(@Param("tenantId") UUID tenantId);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.bill.property.id = :propertyId AND p.status = 'completed' AND p.paidAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalPaymentsByPropertyAndDateRange(@Param("propertyId") UUID propertyId, 
                                                       @Param("startDate") OffsetDateTime startDate, 
                                                       @Param("endDate") OffsetDateTime endDate);
    
    @Modifying
    @Transactional
    @Query("UPDATE Payment p SET p.status = :status, p.paidAt = :paidAt WHERE p.id = :paymentId")
    void updatePaymentStatus(@Param("paymentId") UUID paymentId, 
                             @Param("status") String status, 
                             @Param("paidAt") OffsetDateTime paidAt);
}
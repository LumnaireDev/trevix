package com.trevix.property_management.repository;

import com.trevix.property_management.entity.MonthlyBill;
import com.trevix.property_management.enums.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MonthlyBillRepository extends JpaRepository<MonthlyBill, UUID> {
    
    List<MonthlyBill> findByTenant_UserId(UUID tenantId);
    
    List<MonthlyBill> findByRoomId(UUID roomId);
    
    List<MonthlyBill> findByStatus(BillStatus status);
    
    @Query("SELECT b FROM MonthlyBill b WHERE b.tenant.userId = :tenantId AND b.status = 'PENDING'")
    List<MonthlyBill> findPendingBillsByTenant(@Param("tenantId") UUID tenantId);
    
    @Query("SELECT b FROM MonthlyBill b WHERE b.dueDate < :date AND b.status = 'PENDING'")
    List<MonthlyBill> findOverdueBills(@Param("date") LocalDate date);
    
    @Query("SELECT SUM(b.totalAmount) FROM MonthlyBill b WHERE b.property.id = :propertyId AND b.status = 'PAID' AND EXTRACT(MONTH FROM b.billingMonth) = :month AND EXTRACT(YEAR FROM b.billingMonth) = :year")
    BigDecimal getTotalCollectedForMonth(@Param("propertyId") UUID propertyId, @Param("month") int month, @Param("year") int year);
    
    @Query("SELECT SUM(b.totalAmount) FROM MonthlyBill b WHERE b.property.id = :propertyId AND b.status = 'PENDING'")
    BigDecimal getTotalPendingAmount(@Param("propertyId") UUID propertyId);
    
    @Query("SELECT COUNT(b) FROM MonthlyBill b WHERE b.property.id = :propertyId AND b.status = 'OVERDUE'")
    long countOverdueBillsByProperty(@Param("propertyId") UUID propertyId);
    
    @Modifying
    @Transactional
    @Query("UPDATE MonthlyBill b SET b.status = 'OVERDUE' WHERE b.dueDate < :date AND b.status = 'PENDING'")
    void updateOverdueBills(@Param("date") LocalDate date);
    
    @Modifying
    @Transactional
    @Query("UPDATE MonthlyBill b SET b.status = 'PAID', b.paidAt = :paidAt, b.xenditInvoiceId = :invoiceId WHERE b.id = :billId")
    void markAsPaid(@Param("billId") UUID billId, @Param("paidAt") OffsetDateTime paidAt, @Param("invoiceId") String invoiceId);
}
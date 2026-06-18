package com.trevix.property_management.repository;

import com.trevix.property_management.entity.AdminSubscriptionPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AdminSubscriptionPaymentRepository extends JpaRepository<AdminSubscriptionPayment, UUID> {
    
    List<AdminSubscriptionPayment> findByAdmin_UserId(UUID adminId);
    
    List<AdminSubscriptionPayment> findByStatus(String status);
    
    @Query("SELECT asp FROM AdminSubscriptionPayment asp WHERE asp.admin.userId = :adminId AND asp.status = 'completed' ORDER BY asp.createdAt DESC")
    List<AdminSubscriptionPayment> findSuccessfulPaymentsByAdmin(@Param("adminId") UUID adminId);
    
    @Query("SELECT asp FROM AdminSubscriptionPayment asp WHERE asp.endDate < :date AND asp.status = 'completed'")
    List<AdminSubscriptionPayment> findExpiredPayments(@Param("date") LocalDate date);
}
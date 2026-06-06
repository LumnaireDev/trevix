package com.trevix.property_management.service;

import com.trevix.property_management.dto.request.GenerateBillRequest;
import com.trevix.property_management.dto.request.PaymentRequest;
import com.trevix.property_management.dto.response.MonthlyBillResponse;
import com.trevix.property_management.dto.response.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BillingService {
    MonthlyBillResponse generateBill(GenerateBillRequest request);
    List<MonthlyBillResponse> generateMonthlyBills(UUID propertyId, LocalDate billingMonth);
    MonthlyBillResponse getBillById(UUID billId);
    List<MonthlyBillResponse> getBillsByTenant(UUID tenantId);
    List<MonthlyBillResponse> getBillsByRoom(UUID roomId);
    List<MonthlyBillResponse> getPendingBillsByTenant(UUID tenantId);
    Page<MonthlyBillResponse> getBillsByProperty(UUID propertyId, Pageable pageable);
    PaymentResponse processPayment(UUID billId, PaymentRequest request);
    PaymentResponse getPaymentById(UUID paymentId);
    List<PaymentResponse> getPaymentsByBill(UUID billId);
    List<PaymentResponse> getPaymentsByTenant(UUID tenantId);
    BigDecimal getOutstandingBalance(UUID tenantId);
    BigDecimal getTotalCollected(UUID propertyId, int month, int year);
    void sendPaymentReminders();
    void processOverdueBills();
}
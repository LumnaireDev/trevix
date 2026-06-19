package com.trevix.property_management.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.trevix.property_management.dto.request.GenerateBillRequest;
import com.trevix.property_management.dto.request.PaymentRequest;
import com.trevix.property_management.dto.response.ApiResponse;
import com.trevix.property_management.dto.response.MonthlyBillResponse;
import com.trevix.property_management.dto.response.PaymentResponse;
import com.trevix.property_management.service.BillingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LANDLORD')")
public class BillingController {

    private final BillingService billingService;

    @PostMapping("/invoices")
    public ResponseEntity<ApiResponse<MonthlyBillResponse>> createInvoice(
            @Valid @RequestBody GenerateBillRequest request,
            HttpServletRequest httpRequest) {
        MonthlyBillResponse response = billingService.generateBill(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Invoice created", httpRequest.getRequestURI()));
    }

    @GetMapping("/invoices")
    public ResponseEntity<ApiResponse<List<MonthlyBillResponse>>> getInvoicesByProperty(
            @RequestParam UUID propertyId,
            HttpServletRequest httpRequest) {
        List<MonthlyBillResponse> response = billingService.getBillsByProperty(propertyId);
        return ResponseEntity.ok(ApiResponse.success(response, "Invoices retrieved", httpRequest.getRequestURI()));
    }

    @GetMapping("/invoices/{billId}")
    public ResponseEntity<ApiResponse<MonthlyBillResponse>> getInvoice(
            @PathVariable UUID billId,
            HttpServletRequest httpRequest) {
        MonthlyBillResponse response = billingService.getBillById(billId);
        return ResponseEntity.ok(ApiResponse.success(response, "Invoice retrieved", httpRequest.getRequestURI()));
    }

    @GetMapping("/invoices/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<List<MonthlyBillResponse>>> getInvoicesByTenant(
            @PathVariable UUID tenantId,
            HttpServletRequest httpRequest) {
        List<MonthlyBillResponse> response = billingService.getBillsByTenant(tenantId);
        return ResponseEntity.ok(ApiResponse.success(response, "Tenant invoices retrieved", httpRequest.getRequestURI()));
    }

    @PostMapping("/invoices/{billId}/pay")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @PathVariable UUID billId,
            @Valid @RequestBody PaymentRequest request,
            HttpServletRequest httpRequest) {
        PaymentResponse response = billingService.processPayment(billId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Payment processed", httpRequest.getRequestURI()));
    }

    @GetMapping("/invoices/{billId}/payments")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByBill(
            @PathVariable UUID billId,
            HttpServletRequest httpRequest) {
        List<PaymentResponse> response = billingService.getPaymentsByBill(billId);
        return ResponseEntity.ok(ApiResponse.success(response, "Payments retrieved", httpRequest.getRequestURI()));
    }
}
package com.trevix.property_management.service;

import com.trevix.property_management.dto.request.GenerateBillRequest;
import com.trevix.property_management.dto.request.PaymentRequest;
import com.trevix.property_management.dto.response.MonthlyBillResponse;
import com.trevix.property_management.dto.response.PaymentResponse;
import com.trevix.property_management.entity.MonthlyBill;
import com.trevix.property_management.entity.Payment;
import com.trevix.property_management.entity.Room;
import com.trevix.property_management.entity.Tenant;
import com.trevix.property_management.enums.BillStatus;
import com.trevix.property_management.enums.ErrorCode;
import com.trevix.property_management.enums.PaymentStatus;
import com.trevix.property_management.exception.AppException;
import com.trevix.property_management.repository.MonthlyBillRepository;
import com.trevix.property_management.repository.PaymentRepository;
import com.trevix.property_management.repository.RoomRepository;
import com.trevix.property_management.repository.TenantRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BillingService {

    private final MonthlyBillRepository billRepository;
    private final PaymentRepository paymentRepository;
    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;

    @Transactional
    public MonthlyBillResponse generateBill(GenerateBillRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Room not found: " + request.getRoomId()));

        Tenant tenant = tenantRepository.findById(request.getTenantId())
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Tenant not found: " + request.getTenantId()));

        BigDecimal rent = request.getRentAmount() != null ? request.getRentAmount() : room.getBaseRent();
        BigDecimal water = request.getWaterAmount() != null ? request.getWaterAmount() : room.getWaterFee();
        BigDecimal electricity = request.getElectricityAmount() != null ? request.getElectricityAmount() : room.getElectricityFee();
        BigDecimal total = rent.add(water).add(electricity);

        MonthlyBill bill = new MonthlyBill();
        bill.setProperty(room.getProperty());
        bill.setRoom(room);
        bill.setTenant(tenant);
        bill.setBillingMonth(request.getBillingMonth());
        bill.setDueDate(request.getDueDate());
        bill.setRentAmount(rent);
        bill.setWaterAmount(water);
        bill.setElectricityAmount(electricity);
        bill.setTotalAmount(total);
        bill.setStatus(BillStatus.PENDING);

        MonthlyBill saved = billRepository.save(bill);
        log.info("Bill generated for tenant: {} room: {}", tenant.getUserId(), room.getRoomNumber());
        return toResponse(saved);
    }

    public MonthlyBillResponse getBillById(UUID billId) {
        return toResponse(findBillById(billId));
    }

    public List<MonthlyBillResponse> getBillsByTenant(UUID tenantId) {
        return billRepository.findByTenant_UserId(tenantId)
            .stream().map(this::toResponse).toList();
    }

    public List<MonthlyBillResponse> getBillsByProperty(UUID propertyId) {
        return billRepository.findByPropertyId(propertyId)
            .stream().map(this::toResponse).toList();
    }

    public List<MonthlyBillResponse> getPendingBillsByTenant(UUID tenantId) {
        return billRepository.findPendingByTenant(tenantId)
            .stream().map(this::toResponse).toList();
    }

    @Transactional
    public PaymentResponse processPayment(UUID billId, PaymentRequest request) {
        MonthlyBill bill = findBillById(billId);

        if (bill.getStatus() == BillStatus.PAID)
            throw new AppException(ErrorCode.BAD_REQUEST, "Bill is already paid");

        Payment payment = new Payment();
        payment.setBill(bill);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setTransactionId(request.getTransactionId());
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(OffsetDateTime.now());

        paymentRepository.save(payment);

        bill.setStatus(BillStatus.PAID);
        bill.setPaidAt(OffsetDateTime.now());
        billRepository.save(bill);

        log.info("Payment processed for bill: {}", billId);
        return toPaymentResponse(payment);
    }

    public List<PaymentResponse> getPaymentsByBill(UUID billId) {
        return paymentRepository.findByBill_Id(billId)
            .stream().map(this::toPaymentResponse).toList();
    }

    public List<PaymentResponse> getPaymentsByTenant(UUID tenantId) {
        return paymentRepository.findByTenantId(tenantId)
            .stream().map(this::toPaymentResponse).toList();
    }

    // ── Mappers (inline — no separate mapper class needed for billing) ──

    private MonthlyBillResponse toResponse(MonthlyBill bill) {
        MonthlyBillResponse r = new MonthlyBillResponse();
        r.setId(bill.getId());
        r.setPropertyId(bill.getProperty() != null ? bill.getProperty().getId() : null);
        r.setPropertyName(bill.getProperty() != null ? bill.getProperty().getName() : null);
        r.setRoomId(bill.getRoom() != null ? bill.getRoom().getId() : null);
        r.setRoomNumber(bill.getRoom() != null ? bill.getRoom().getRoomNumber() : null);
        r.setTenantId(bill.getTenant() != null ? bill.getTenant().getUserId() : null);
        r.setTenantName(bill.getTenant() != null && bill.getTenant().getUser() != null
            ? bill.getTenant().getUser().getFullName() : null);
        r.setBillingMonth(bill.getBillingMonth());
        r.setRentAmount(bill.getRentAmount());
        r.setWaterAmount(bill.getWaterAmount());
        r.setElectricityAmount(bill.getElectricityAmount());
        r.setTotalAmount(bill.getTotalAmount());
        r.setDueDate(bill.getDueDate());
        r.setStatus(bill.getStatus());
        r.setPaidAt(bill.getPaidAt());
        r.setCreatedAt(bill.getCreatedAt());
        r.setUpdatedAt(bill.getUpdatedAt());
        return r;
    }

    private PaymentResponse toPaymentResponse(Payment payment) {
        PaymentResponse r = new PaymentResponse();
        r.setId(payment.getId());
        r.setBillId(payment.getBill() != null ? payment.getBill().getId() : null);
        r.setTenantName(payment.getBill() != null
            && payment.getBill().getTenant() != null
            && payment.getBill().getTenant().getUser() != null
            ? payment.getBill().getTenant().getUser().getFullName() : null);
        r.setRoomNumber(payment.getBill() != null && payment.getBill().getRoom() != null
            ? payment.getBill().getRoom().getRoomNumber() : null);
        r.setAmount(payment.getAmount());
        r.setPaymentMethod(payment.getPaymentMethod());
        r.setTransactionId(payment.getTransactionId());
        r.setStatus(payment.getStatus());
        r.setPaidAt(payment.getPaidAt());
        r.setCreatedAt(payment.getCreatedAt());
        return r;
    }

    private MonthlyBill findBillById(UUID billId) {
        return billRepository.findById(billId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Bill not found: " + billId));
    }
}
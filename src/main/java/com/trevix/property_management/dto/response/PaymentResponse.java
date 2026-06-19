package com.trevix.property_management.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import com.trevix.property_management.enums.PaymentStatus;

@Data
public class PaymentResponse {
    private UUID id;
    private UUID billId;
    private String tenantName;
    private String roomNumber;
    private BigDecimal amount;
    private String paymentMethod;
    private String transactionId;
    private PaymentStatus status;
    private OffsetDateTime paidAt;
    private OffsetDateTime createdAt;
}
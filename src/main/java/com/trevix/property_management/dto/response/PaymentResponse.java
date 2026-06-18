package com.trevix.property_management.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class PaymentResponse {
    private UUID id;
    private UUID billId;
    private BigDecimal amount;
    private String paymentMethod;
    private String transactionId;
    private String status;
    private OffsetDateTime paidAt;
    private String xenditChargeId;
    private OffsetDateTime createdAt;
}
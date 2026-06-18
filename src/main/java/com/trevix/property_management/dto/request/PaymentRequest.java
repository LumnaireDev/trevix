package com.trevix.property_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequest {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;     // e.g. "GCASH", "CARD", "BANK_TRANSFER", "CASH"

    private String transactionId;     // external ref, optional for manual payments
}
package com.trevix.property_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class GenerateBillRequest {

    @NotNull(message = "Room ID is required")
    private UUID roomId;

    @NotNull(message = "Tenant ID is required")
    private UUID tenantId;

    @NotNull(message = "Billing month is required")
    private LocalDate billingMonth;   // e.g. 2025-06-01 — always first of month

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    // Override amounts — if null, service pulls from Room entity
    private BigDecimal rentAmount;
    private BigDecimal waterAmount;
    private BigDecimal electricityAmount;
}
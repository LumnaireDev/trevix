package com.trevix.property_management.dto.response;

import lombok.Data;
import com.trevix.property_management.enums.BillStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class MonthlyBillResponse {
    private UUID id;
    private UUID propertyId;
    private String propertyName;
    private UUID roomId;
    private String roomNumber;
    private UUID tenantId;
    private String tenantName;
    private LocalDate billingMonth;
    private BigDecimal rentAmount;
    private BigDecimal waterAmount;
    private BigDecimal electricityAmount;
    private BigDecimal totalAmount;
    private LocalDate dueDate;
    private BillStatus status;
    private OffsetDateTime paidAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
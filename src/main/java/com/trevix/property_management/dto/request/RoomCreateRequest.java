package com.trevix.property_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class RoomCreateRequest {

    @NotNull(message = "Property ID is required")
    private UUID propertyId;

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    private Integer floor;

    @NotNull(message = "Base rent is required")
    @Positive(message = "Base rent must be positive")
    private BigDecimal baseRent;

    @PositiveOrZero
    private BigDecimal waterFee = BigDecimal.ZERO;

    @PositiveOrZero
    private BigDecimal electricityFee = BigDecimal.ZERO;

    @Positive
    private Integer capacity = 1;

    private String description;
}
package com.trevix.property_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import com.trevix.property_management.enums.RoomStatus;
import java.math.BigDecimal;

@Data
public class RoomUpdateRequest {

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    private Integer floor;

    @NotNull(message = "Base rent is required")
    @Positive(message = "Base rent must be positive")
    private BigDecimal baseRent;

    @PositiveOrZero
    private BigDecimal waterFee;

    @PositiveOrZero
    private BigDecimal electricityFee;

    @Positive
    private Integer capacity;

    private String description;

    private RoomStatus status;

    private Boolean isActive;
}
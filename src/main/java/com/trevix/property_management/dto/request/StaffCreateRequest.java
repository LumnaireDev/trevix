package com.trevix.property_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import com.trevix.property_management.enums.StaffType;

@Data
public class StaffCreateRequest {
    
    @NotNull(message = "Staff type is required")
    private StaffType staffType;
    
    private LocalDate hireDate;
}
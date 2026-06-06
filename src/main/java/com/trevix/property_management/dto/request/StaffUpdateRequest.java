package com.trevix.property_management.dto.request;

import lombok.Data;
import java.time.LocalDate;
import com.trevix.property_management.enums.StaffType;

@Data
public class StaffUpdateRequest {
    private StaffType staffType;
    private LocalDate hireDate;
    private Boolean isActive;
}

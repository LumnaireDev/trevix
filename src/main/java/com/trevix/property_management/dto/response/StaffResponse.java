package com.trevix.property_management.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import com.trevix.property_management.enums.StaffType;

@Data
@Builder
public class StaffResponse {
    private UUID userId;
    private UserResponse user;
    private StaffType staffType;
    private LocalDate hireDate;
    private Boolean isActive;
    private List<UUID> assignedPropertyIds;
}
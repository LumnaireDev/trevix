package com.trevix.property_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    private String oldPassword;

    @NotBlank(message = "New password is required")
    private String newPassword;
}

package com.trevix.property_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordConfirmRequest {

    @NotBlank(message = "Reset token is required")
    private String token;

    @NotBlank(message = "New password is required")
    private String newPassword;
}

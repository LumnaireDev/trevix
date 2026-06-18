package com.trevix.property_management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TenantRegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;
    private String emergencyContactName;

    @NotBlank(message = "Emergency contact phone is required")
    private String emergencyContactPhone;

    private String emergencyContactRelation;
    private LocalDate moveInDate;
}
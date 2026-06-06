package com.trevix.property_management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {
    
    @Email(message = "Invalid email format")
    private String email;
    
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    private String fullName;
    
    @Size(max = 20, message = "Phone number must be at most 20 characters")
    private String phone;
    
    private Boolean isActive;
}
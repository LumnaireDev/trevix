package com.trevix.property_management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private UserResponse user;
    private UUID userId;
    private String email;
    private String fullName;
    private String role;
    private boolean isAuthenticated;
    private LocalDateTime loginTime;
    private String message;
    
    // For successful login
    public static AuthResponse success(String accessToken, String refreshToken, Long expiresIn, UserResponse user) {
        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(expiresIn)
            .user(user)
            .userId(user != null ? user.getId() : null)
            .email(user != null ? user.getEmail() : null)
            .fullName(user != null ? user.getFullName() : null)
            .role(user != null ? user.getRole().toString() : null)
            .isAuthenticated(true)
            .loginTime(LocalDateTime.now())
            .build();
    }
    
    // For failed login
    public static AuthResponse failure(String message) {
        return AuthResponse.builder()
            .isAuthenticated(false)
            .message(message)
            .loginTime(LocalDateTime.now())
            .build();
    }
    
    // For logout response
    public static AuthResponse logout() {
        return AuthResponse.builder()
            .isAuthenticated(false)
            .message("Successfully logged out")
            .loginTime(LocalDateTime.now())
            .build();
    }
    
    // For token refresh
    public static AuthResponse refresh(String accessToken, Long expiresIn) {
        return AuthResponse.builder()
            .accessToken(accessToken)
            .tokenType("Bearer")
            .expiresIn(expiresIn)
            .isAuthenticated(true)
            .loginTime(LocalDateTime.now())
            .message("Token refreshed successfully")
            .build();
    }
}
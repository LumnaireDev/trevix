package com.trevix.property_management.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.trevix.property_management.dto.request.ChangePasswordRequest;
import com.trevix.property_management.dto.request.ForgotPasswordRequest;
import com.trevix.property_management.dto.request.LoginRequest;
import com.trevix.property_management.dto.request.RefreshTokenRequest;
import com.trevix.property_management.dto.request.ResetPasswordConfirmRequest;
import com.trevix.property_management.dto.request.TenantRegisterRequest;
import com.trevix.property_management.dto.response.ApiResponse;
import com.trevix.property_management.dto.response.AuthResponse;
import com.trevix.property_management.dto.response.UserResponse;
import com.trevix.property_management.security.CustomUserDetails;
import com.trevix.property_management.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request,
                                                             HttpServletRequest httpRequest) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful", httpRequest.getRequestURI()));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody TenantRegisterRequest request,
                                                                HttpServletRequest httpRequest) {
        UserResponse response = authService.registerTenant(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Registration successful", httpRequest.getRequestURI()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request,
                                                               HttpServletRequest httpRequest) {
        AuthResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed", httpRequest.getRequestURI()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authorizationHeader,
                                                      HttpServletRequest httpRequest) {
        authService.logout(authorizationHeader);
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully", httpRequest.getRequestURI()));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@AuthenticationPrincipal CustomUserDetails principal,
                                                              @Valid @RequestBody ChangePasswordRequest request,
                                                              HttpServletRequest httpRequest) {
        authService.changePassword(principal.getId(), request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully", httpRequest.getRequestURI()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request,
                                                              HttpServletRequest httpRequest) {
        authService.resetPassword(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset email sent", httpRequest.getRequestURI()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordConfirmRequest request,
                                                             HttpServletRequest httpRequest) {
        authService.confirmResetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset successful", httpRequest.getRequestURI()));
    }
}

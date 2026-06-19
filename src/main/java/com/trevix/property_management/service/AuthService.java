package com.trevix.property_management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.trevix.property_management.config.JwtTokenProvider;
import com.trevix.property_management.dto.request.LoginRequest;
import com.trevix.property_management.dto.request.TenantRegisterRequest;
import com.trevix.property_management.dto.response.AuthResponse;
import com.trevix.property_management.dto.response.UserResponse;
import com.trevix.property_management.entity.Tenant;
import com.trevix.property_management.entity.User;
import com.trevix.property_management.enums.ErrorCode;
import com.trevix.property_management.enums.UserRole;
import com.trevix.property_management.exception.AppException;
import com.trevix.property_management.mapper.UserMapper;
import com.trevix.property_management.repository.TenantRepository;
import com.trevix.property_management.repository.UserRepository;
import com.trevix.property_management.security.CustomUserDetails;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    public AuthResponse login(LoginRequest request) {
        try {
            User debugUser = userRepository.findByEmail(request.getEmail()).orElse(null);
            log.info("User found: {}, passwordHash: {}", debugUser != null, debugUser != null ? debugUser.getPasswordHash() : "null");

            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User not found: " + userDetails.getId()));

            String accessToken = jwtTokenProvider.generateAccessToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

            return AuthResponse.success(accessToken, refreshToken,
                jwtTokenProvider.getAccessTokenExpiration(), userMapper.toResponse(user));

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Login failed for email: {} — cause: {}", request.getEmail(), e.getMessage(), e);
            throw new AppException(ErrorCode.UNAUTHORIZED, "Invalid email or password");
        }
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateRefreshToken(refreshToken))
            throw new AppException(ErrorCode.UNAUTHORIZED, "Invalid or expired refresh token");

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User not found"));

        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);

        return AuthResponse.success(newAccessToken, refreshToken,
            jwtTokenProvider.getAccessTokenExpiration(), userMapper.toResponse(user));
    }

    public UserResponse registerTenant(TenantRegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent())
            throw new AppException(ErrorCode.DUPLICATE_RESOURCE, "User with this email already exists");

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(UserRole.TENANT);
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        Tenant tenant = new Tenant();
        tenant.setUserId(savedUser.getId());
        tenant.setEmergencyContactName(request.getEmergencyContactName());
        tenant.setEmergencyContactPhone(request.getEmergencyContactPhone());
        tenant.setEmergencyContactRelation(request.getEmergencyContactRelation());
        tenant.setIsActive(true);

        tenantRepository.save(tenant);
        notificationService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFullName());

        log.info("New tenant registered: {}", savedUser.getEmail());
        return userMapper.toResponse(savedUser);
    }

    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            jwtTokenProvider.invalidateToken(token);
            log.info("User logged out");
        }
    }

    public void changePassword(UUID userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User not found with id: " + userId));

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash()))
            throw new AppException(ErrorCode.BAD_REQUEST, "Current password is incorrect");

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed for user: {}", userId);
    }

    public void resetPassword(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String resetToken = jwtTokenProvider.generatePasswordResetToken(email);
            notificationService.sendPasswordResetEmail(email, resetToken);
            log.info("Password reset email sent to: {}", email);
        });
    }

    public void confirmResetPassword(String token, String newPassword) {
        if (!jwtTokenProvider.validatePasswordResetToken(token))
            throw new AppException(ErrorCode.UNAUTHORIZED, "Invalid or expired reset token");

        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User not found with email: " + email));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password reset confirmed for user: {}", email);
    }

    public void validateToken(String token) {
        if (!jwtTokenProvider.validateToken(token))
            throw new AppException(ErrorCode.UNAUTHORIZED, "Invalid or expired token");
    }
}
package com.trevix.property_management.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.trevix.property_management.dto.request.TenantCreateRequest;
import com.trevix.property_management.dto.request.TenantUpdateRequest;
import com.trevix.property_management.dto.response.ApiResponse;
import com.trevix.property_management.dto.response.PropertyResponse;
import com.trevix.property_management.dto.response.TenantDetailResponse;
import com.trevix.property_management.dto.response.TenantResponse;
import com.trevix.property_management.enums.ErrorCode;
import com.trevix.property_management.exception.AppException;
import com.trevix.property_management.security.CustomUserDetails;
import com.trevix.property_management.service.PropertyService;
import com.trevix.property_management.service.TenantService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('LANDLORD')")
public class TenantController {

    private final TenantService tenantService;
    private final PropertyService propertyService;

    @PostMapping("/api/tenants")
    public ResponseEntity<ApiResponse<TenantResponse>> createTenant(
            @Valid @RequestBody TenantCreateRequest request,
            HttpServletRequest httpRequest) {
        TenantResponse response = tenantService.createTenant(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Tenant created", httpRequest.getRequestURI()));
    }

    @GetMapping("/api/tenants/{tenantId}")
    public ResponseEntity<ApiResponse<TenantDetailResponse>> getTenant(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable UUID tenantId,
            HttpServletRequest httpRequest) {
        verifyTenantOwnership(tenantId, principal.getId());
        TenantDetailResponse response = tenantService.getTenantDetail(tenantId);
        return ResponseEntity.ok(ApiResponse.success(response, "Tenant retrieved", httpRequest.getRequestURI()));
    }

    @PutMapping("/api/tenants/{tenantId}")
    public ResponseEntity<ApiResponse<TenantResponse>> updateTenant(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable UUID tenantId,
            @Valid @RequestBody TenantUpdateRequest request,
            HttpServletRequest httpRequest) {
        verifyTenantOwnership(tenantId, principal.getId());
        TenantResponse response = tenantService.updateTenant(tenantId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Tenant updated", httpRequest.getRequestURI()));
    }

    @DeleteMapping("/api/tenants/{tenantId}")
    public ResponseEntity<ApiResponse<Void>> deleteTenant(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable UUID tenantId,
            HttpServletRequest httpRequest) {
        verifyTenantOwnership(tenantId, principal.getId());
        tenantService.deleteTenant(tenantId);
        return ResponseEntity.ok(ApiResponse.success(null, "Tenant deleted", httpRequest.getRequestURI()));
    }

    @PatchMapping("/api/tenants/{tenantId}/activate")
    public ResponseEntity<ApiResponse<Void>> activateTenant(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable UUID tenantId,
            HttpServletRequest httpRequest) {
        verifyTenantOwnership(tenantId, principal.getId());
        tenantService.activateTenant(tenantId);
        return ResponseEntity.ok(ApiResponse.success(null, "Tenant activated", httpRequest.getRequestURI()));
    }

    @PatchMapping("/api/tenants/{tenantId}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateTenant(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable UUID tenantId,
            HttpServletRequest httpRequest) {
        verifyTenantOwnership(tenantId, principal.getId());
        tenantService.deactivateTenant(tenantId);
        return ResponseEntity.ok(ApiResponse.success(null, "Tenant deactivated", httpRequest.getRequestURI()));
    }

    @GetMapping("/api/properties/{propertyId}/tenants")
    public ResponseEntity<ApiResponse<List<TenantResponse>>> getTenantsByProperty(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable UUID propertyId,
            HttpServletRequest httpRequest) {
        PropertyResponse property = propertyService.getPropertyById(propertyId);
        if (!property.getOwnerId().equals(principal.getId()))
            throw new AppException(ErrorCode.FORBIDDEN, "You do not have access to this property");
        List<TenantResponse> response = tenantService.getActiveTenantsByProperty(propertyId);
        return ResponseEntity.ok(ApiResponse.success(response, "Tenants retrieved", httpRequest.getRequestURI()));
    }

    private void verifyTenantOwnership(UUID tenantId, UUID ownerId) {
        if (!tenantService.isTenantManagedByOwner(tenantId, ownerId))
            throw new AppException(ErrorCode.FORBIDDEN, "You do not have access to this tenant");
    }
}
package com.trevix.property_management.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.trevix.property_management.dto.request.PropertyCreateRequest;
import com.trevix.property_management.dto.request.PropertyUpdateRequest;
import com.trevix.property_management.dto.response.ApiResponse;
import com.trevix.property_management.dto.response.PropertyDetailResponse;
import com.trevix.property_management.dto.response.PropertyResponse;
import com.trevix.property_management.enums.ErrorCode;
import com.trevix.property_management.exception.AppException;
import com.trevix.property_management.security.CustomUserDetails;
import com.trevix.property_management.service.PropertyService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping
    public ResponseEntity<ApiResponse<PropertyResponse>> createProperty(@AuthenticationPrincipal CustomUserDetails principal,
                                                                          @Valid @RequestBody PropertyCreateRequest request,
                                                                          HttpServletRequest httpRequest) {
        PropertyResponse response = propertyService.createProperty(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Property created", httpRequest.getRequestURI()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PropertyResponse>>> getProperties(@AuthenticationPrincipal CustomUserDetails principal,
                                                                               HttpServletRequest httpRequest) {
        List<PropertyResponse> response = propertyService.getPropertiesByAdmin(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Properties retrieved", httpRequest.getRequestURI()));
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<ApiResponse<PropertyResponse>> getProperty(@AuthenticationPrincipal CustomUserDetails principal,
                                                                       @PathVariable UUID propertyId,
                                                                       HttpServletRequest httpRequest) {
        PropertyResponse response = verifyOwnership(propertyId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Property retrieved", httpRequest.getRequestURI()));
    }

    @GetMapping("/{propertyId}/detail")
    public ResponseEntity<ApiResponse<PropertyDetailResponse>> getPropertyDetail(@AuthenticationPrincipal CustomUserDetails principal,
                                                                                   @PathVariable UUID propertyId,
                                                                                   HttpServletRequest httpRequest) {
        verifyOwnership(propertyId, principal.getId());
        PropertyDetailResponse response = propertyService.getPropertyDetail(propertyId);
        return ResponseEntity.ok(ApiResponse.success(response, "Property detail retrieved", httpRequest.getRequestURI()));
    }

    @PutMapping("/{propertyId}")
    public ResponseEntity<ApiResponse<PropertyResponse>> updateProperty(@AuthenticationPrincipal CustomUserDetails principal,
                                                                          @PathVariable UUID propertyId,
                                                                          @Valid @RequestBody PropertyUpdateRequest request,
                                                                          HttpServletRequest httpRequest) {
        verifyOwnership(propertyId, principal.getId());
        PropertyResponse response = propertyService.updateProperty(propertyId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Property updated", httpRequest.getRequestURI()));
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<ApiResponse<Void>> deleteProperty(@AuthenticationPrincipal CustomUserDetails principal,
                                                              @PathVariable UUID propertyId,
                                                              HttpServletRequest httpRequest) {
        verifyOwnership(propertyId, principal.getId());
        propertyService.softDeleteProperty(propertyId);
        return ResponseEntity.ok(ApiResponse.success(null, "Property deleted", httpRequest.getRequestURI()));
    }

    @PatchMapping("/{propertyId}/activate")
    public ResponseEntity<ApiResponse<Void>> activateProperty(@AuthenticationPrincipal CustomUserDetails principal,
                                                                @PathVariable UUID propertyId,
                                                                HttpServletRequest httpRequest) {
        verifyOwnership(propertyId, principal.getId());
        propertyService.activateProperty(propertyId);
        return ResponseEntity.ok(ApiResponse.success(null, "Property activated", httpRequest.getRequestURI()));
    }

    @PatchMapping("/{propertyId}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateProperty(@AuthenticationPrincipal CustomUserDetails principal,
                                                                  @PathVariable UUID propertyId,
                                                                  HttpServletRequest httpRequest) {
        verifyOwnership(propertyId, principal.getId());
        propertyService.deactivateProperty(propertyId);
        return ResponseEntity.ok(ApiResponse.success(null, "Property deactivated", httpRequest.getRequestURI()));
    }

    private PropertyResponse verifyOwnership(UUID propertyId, UUID adminId) {
        PropertyResponse property = propertyService.getPropertyById(propertyId);
        if (!property.getAdminId().equals(adminId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "You do not have access to this property");
        }
        return property;
    }
}

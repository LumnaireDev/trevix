package com.trevix.property_management.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.trevix.property_management.dto.request.MaintenanceCreateRequest;
import com.trevix.property_management.dto.request.MaintenanceRequestResponse;
import com.trevix.property_management.dto.request.MaintenanceStatusUpdateRequest;
import com.trevix.property_management.dto.response.ApiResponse;
import com.trevix.property_management.security.CustomUserDetails;
import com.trevix.property_management.service.MaintenanceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LANDLORD')")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PostMapping
    public ResponseEntity<ApiResponse<MaintenanceRequestResponse>> createRequest(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody MaintenanceCreateRequest request,
            HttpServletRequest httpRequest) {
        MaintenanceRequestResponse response = maintenanceService.createRequest(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Request created", httpRequest.getRequestURI()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MaintenanceRequestResponse>>> getAll(
            @AuthenticationPrincipal CustomUserDetails principal,
            HttpServletRequest httpRequest) {
        List<MaintenanceRequestResponse> response = maintenanceService.getByOwner(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Requests retrieved", httpRequest.getRequestURI()));
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<ApiResponse<List<MaintenanceRequestResponse>>> getByProperty(
            @PathVariable UUID propertyId,
            HttpServletRequest httpRequest) {
        List<MaintenanceRequestResponse> response = maintenanceService.getByProperty(propertyId);
        return ResponseEntity.ok(ApiResponse.success(response, "Requests retrieved", httpRequest.getRequestURI()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MaintenanceRequestResponse>> getById(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        MaintenanceRequestResponse response = maintenanceService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Request retrieved", httpRequest.getRequestURI()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<MaintenanceRequestResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody MaintenanceStatusUpdateRequest request,
            HttpServletRequest httpRequest) {
        MaintenanceRequestResponse response = maintenanceService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Status updated", httpRequest.getRequestURI()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRequest(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        maintenanceService.deleteRequest(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Request deleted", httpRequest.getRequestURI()));
    }
}
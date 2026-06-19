package com.trevix.property_management.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.trevix.property_management.dto.request.AnnouncementCreateRequest;
import com.trevix.property_management.dto.response.AnnouncementResponse;
import com.trevix.property_management.dto.response.ApiResponse;
import com.trevix.property_management.security.CustomUserDetails;
import com.trevix.property_management.service.AnnouncementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LANDLORD')")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping
    public ResponseEntity<ApiResponse<AnnouncementResponse>> create(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody AnnouncementCreateRequest request,
            HttpServletRequest httpRequest) {
        AnnouncementResponse response = announcementService.create(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Announcement created", httpRequest.getRequestURI()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AnnouncementResponse>>> getAll(
            @AuthenticationPrincipal CustomUserDetails principal,
            HttpServletRequest httpRequest) {
        List<AnnouncementResponse> response = announcementService.getByOwner(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Announcements retrieved", httpRequest.getRequestURI()));
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<ApiResponse<List<AnnouncementResponse>>> getByProperty(
            @PathVariable UUID propertyId,
            HttpServletRequest httpRequest) {
        List<AnnouncementResponse> response = announcementService.getByProperty(propertyId);
        return ResponseEntity.ok(ApiResponse.success(response, "Announcements retrieved", httpRequest.getRequestURI()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> getById(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        AnnouncementResponse response = announcementService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Announcement retrieved", httpRequest.getRequestURI()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody AnnouncementCreateRequest request,
            HttpServletRequest httpRequest) {
        AnnouncementResponse response = announcementService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Announcement updated", httpRequest.getRequestURI()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        announcementService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Announcement deleted", httpRequest.getRequestURI()));
    }
}
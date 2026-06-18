package com.trevix.property_management.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.trevix.property_management.dto.request.RoomCreateRequest;
import com.trevix.property_management.dto.request.RoomUpdateRequest;
import com.trevix.property_management.dto.response.ApiResponse;
import com.trevix.property_management.dto.response.PropertyResponse;
import com.trevix.property_management.dto.response.RoomDetailResponse;
import com.trevix.property_management.dto.response.RoomResponse;
import com.trevix.property_management.enums.ErrorCode;
import com.trevix.property_management.exception.AppException;
import com.trevix.property_management.security.CustomUserDetails;
import com.trevix.property_management.service.PropertyService;
import com.trevix.property_management.service.RoomService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoomController {

    private final RoomService roomService;
    private final PropertyService propertyService;

    @PostMapping("/api/properties/{propertyId}/rooms")
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(@AuthenticationPrincipal CustomUserDetails principal,
                                                                  @PathVariable UUID propertyId,
                                                                  @Valid @RequestBody RoomCreateRequest request,
                                                                  HttpServletRequest httpRequest) {
        verifyPropertyOwnership(propertyId, principal.getId());
        RoomResponse response = roomService.createRoom(propertyId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Room created", httpRequest.getRequestURI()));
    }

    @GetMapping("/api/properties/{propertyId}/rooms")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getRoomsByProperty(@AuthenticationPrincipal CustomUserDetails principal,
                                                                                @PathVariable UUID propertyId,
                                                                                HttpServletRequest httpRequest) {
        verifyPropertyOwnership(propertyId, principal.getId());
        List<RoomResponse> response = roomService.getRoomsByProperty(propertyId);
        return ResponseEntity.ok(ApiResponse.success(response, "Rooms retrieved", httpRequest.getRequestURI()));
    }

    @GetMapping("/api/properties/{propertyId}/rooms/available")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAvailableRooms(@AuthenticationPrincipal CustomUserDetails principal,
                                                                               @PathVariable UUID propertyId,
                                                                               HttpServletRequest httpRequest) {
        verifyPropertyOwnership(propertyId, principal.getId());
        List<RoomResponse> response = roomService.getAvailableRoomsByProperty(propertyId);
        return ResponseEntity.ok(ApiResponse.success(response, "Available rooms retrieved", httpRequest.getRequestURI()));
    }

    @GetMapping("/api/rooms/{roomId}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoom(@AuthenticationPrincipal CustomUserDetails principal,
                                                               @PathVariable UUID roomId,
                                                               HttpServletRequest httpRequest) {
        RoomResponse response = verifyRoomOwnership(roomId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Room retrieved", httpRequest.getRequestURI()));
    }

    @GetMapping("/api/rooms/{roomId}/detail")
    public ResponseEntity<ApiResponse<RoomDetailResponse>> getRoomDetail(@AuthenticationPrincipal CustomUserDetails principal,
                                                                           @PathVariable UUID roomId,
                                                                           HttpServletRequest httpRequest) {
        verifyRoomOwnership(roomId, principal.getId());
        RoomDetailResponse response = roomService.getRoomDetail(roomId);
        return ResponseEntity.ok(ApiResponse.success(response, "Room detail retrieved", httpRequest.getRequestURI()));
    }

    @PutMapping("/api/rooms/{roomId}")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(@AuthenticationPrincipal CustomUserDetails principal,
                                                                  @PathVariable UUID roomId,
                                                                  @Valid @RequestBody RoomUpdateRequest request,
                                                                  HttpServletRequest httpRequest) {
        verifyRoomOwnership(roomId, principal.getId());
        RoomResponse response = roomService.updateRoom(roomId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Room updated", httpRequest.getRequestURI()));
    }

    @DeleteMapping("/api/rooms/{roomId}")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@AuthenticationPrincipal CustomUserDetails principal,
                                                          @PathVariable UUID roomId,
                                                          HttpServletRequest httpRequest) {
        verifyRoomOwnership(roomId, principal.getId());
        roomService.deleteRoom(roomId);
        return ResponseEntity.ok(ApiResponse.success(null, "Room deleted", httpRequest.getRequestURI()));
    }

    @PatchMapping("/api/rooms/{roomId}/activate")
    public ResponseEntity<ApiResponse<Void>> activateRoom(@AuthenticationPrincipal CustomUserDetails principal,
                                                            @PathVariable UUID roomId,
                                                            HttpServletRequest httpRequest) {
        verifyRoomOwnership(roomId, principal.getId());
        roomService.activateRoom(roomId);
        return ResponseEntity.ok(ApiResponse.success(null, "Room activated", httpRequest.getRequestURI()));
    }

    @PatchMapping("/api/rooms/{roomId}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateRoom(@AuthenticationPrincipal CustomUserDetails principal,
                                                              @PathVariable UUID roomId,
                                                              HttpServletRequest httpRequest) {
        verifyRoomOwnership(roomId, principal.getId());
        roomService.deactivateRoom(roomId);
        return ResponseEntity.ok(ApiResponse.success(null, "Room deactivated", httpRequest.getRequestURI()));
    }

    private void verifyPropertyOwnership(UUID propertyId, UUID adminId) {
        PropertyResponse property = propertyService.getPropertyById(propertyId);
        if (!property.getAdminId().equals(adminId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "You do not have access to this property");
        }
    }

    private RoomResponse verifyRoomOwnership(UUID roomId, UUID adminId) {
        RoomResponse room = roomService.getRoomById(roomId);
        PropertyResponse property = propertyService.getPropertyById(room.getPropertyId());
        if (!property.getAdminId().equals(adminId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "You do not have access to this room");
        }
        return room;
    }
}

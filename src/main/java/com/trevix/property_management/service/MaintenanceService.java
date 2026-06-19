package com.trevix.property_management.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.trevix.property_management.dto.request.MaintenanceCreateRequest;
import com.trevix.property_management.dto.request.MaintenanceRequestResponse;
import com.trevix.property_management.dto.request.MaintenanceStatusUpdateRequest;
import com.trevix.property_management.entity.MaintenanceRequest;
import com.trevix.property_management.entity.Property;
import com.trevix.property_management.entity.Room;
import com.trevix.property_management.enums.ErrorCode;
import com.trevix.property_management.enums.MaintenanceStatus;
import com.trevix.property_management.exception.AppException;
import com.trevix.property_management.repository.MaintenanceRequestRepository;
import com.trevix.property_management.repository.PropertyRepository;
import com.trevix.property_management.repository.RoomRepository;
import com.trevix.property_management.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaintenanceService {

    private final MaintenanceRequestRepository maintenanceRepository;
    private final PropertyRepository propertyRepository;
    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;

    @Transactional
    public MaintenanceRequestResponse createRequest(UUID requesterId, MaintenanceCreateRequest request) {
        Property property = propertyRepository.findById(request.getPropertyId())
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Property not found: " + request.getPropertyId()));

        Room room = roomRepository.findById(request.getRoomId())
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Room not found: " + request.getRoomId()));

        MaintenanceRequest entity = new MaintenanceRequest();
        entity.setProperty(property);
        entity.setRoom(room);
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setPriority(request.getPriority());
        entity.setStatus(MaintenanceStatus.PENDING);

        // Optionally link tenant if requester is a tenant
        tenantRepository.findById(requesterId).ifPresent(entity::setTenant);

        MaintenanceRequest saved = maintenanceRepository.save(entity);
        log.info("Maintenance request created: {} for property: {}", saved.getId(), property.getId());
        return toResponse(saved);
    }

    public List<MaintenanceRequestResponse> getByProperty(UUID propertyId) {
        return maintenanceRepository.findByProperty_Id(propertyId)
            .stream().map(this::toResponse).toList();
    }

    public List<MaintenanceRequestResponse> getByOwner(UUID ownerId) {
        return maintenanceRepository.findByOwnerId(ownerId)
            .stream().map(this::toResponse).toList();
    }

    public MaintenanceRequestResponse getById(UUID id) {
        return toResponse(findById(id));
    }

    @Transactional
    public MaintenanceRequestResponse updateStatus(UUID id, MaintenanceStatusUpdateRequest request) {
        MaintenanceRequest entity = findById(id);

        OffsetDateTime completedAt = request.getStatus() == MaintenanceStatus.COMPLETED
            ? OffsetDateTime.now() : entity.getCompletedAt();

        maintenanceRepository.updateStatus(id, request.getStatus(), completedAt);

        entity.setStatus(request.getStatus());
        entity.setCompletedAt(completedAt);

        log.info("Maintenance request {} status updated to {}", id, request.getStatus());
        return toResponse(entity);
    }

    @Transactional
    public void deleteRequest(UUID id) {
        if (!maintenanceRepository.existsById(id))
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Maintenance request not found: " + id);
        maintenanceRepository.deleteById(id);
        log.info("Maintenance request deleted: {}", id);
    }

    private MaintenanceRequest findById(UUID id) {
        return maintenanceRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Maintenance request not found: " + id));
    }

    private MaintenanceRequestResponse toResponse(MaintenanceRequest m) {
        MaintenanceRequestResponse r = new MaintenanceRequestResponse();
        r.setId(m.getId());
        r.setPropertyId(m.getProperty() != null ? m.getProperty().getId() : null);
        r.setPropertyName(m.getProperty() != null ? m.getProperty().getName() : null);
        r.setRoomId(m.getRoom() != null ? m.getRoom().getId() : null);
        r.setRoomNumber(m.getRoom() != null ? m.getRoom().getRoomNumber() : null);
        r.setTenantId(m.getTenant() != null ? m.getTenant().getUserId() : null);
        r.setTenantName(m.getTenant() != null && m.getTenant().getUser() != null
            ? m.getTenant().getUser().getFullName() : null);
        r.setTitle(m.getTitle());
        r.setDescription(m.getDescription());
        r.setStatus(m.getStatus());
        r.setPriority(m.getPriority());
        r.setCompletedAt(m.getCompletedAt());
        r.setCreatedAt(m.getCreatedAt());
        r.setUpdatedAt(m.getUpdatedAt());
        return r;
    }
}
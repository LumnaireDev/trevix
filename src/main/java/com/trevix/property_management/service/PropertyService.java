package com.trevix.property_management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.trevix.property_management.dto.request.PropertyCreateRequest;
import com.trevix.property_management.dto.request.PropertyUpdateRequest;
import com.trevix.property_management.dto.response.PropertyDetailResponse;
import com.trevix.property_management.dto.response.PropertyResponse;
import com.trevix.property_management.entity.Admin;
import com.trevix.property_management.entity.Property;
import com.trevix.property_management.enums.ErrorCode;
import com.trevix.property_management.enums.PropertyStatus;
import com.trevix.property_management.exception.AppException;
import com.trevix.property_management.mapper.PropertyMapper;
import com.trevix.property_management.repository.AdminRepository;
import com.trevix.property_management.repository.PropertyRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final AdminRepository adminRepository;
    private final PropertyMapper propertyMapper;
    private final SubscriptionService subscriptionService;

    @Transactional
    public PropertyResponse createProperty(UUID adminId, PropertyCreateRequest request) {
        Admin admin = adminRepository.findById(adminId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Admin not found: " + adminId));

        if (!subscriptionService.canAddProperty(adminId)) {
            long current = propertyRepository.countActiveByAdmin(adminId);
            int limit = subscriptionService.getPropertyLimit(admin.getSubscriptionPlan());
            throw new AppException(ErrorCode.FORBIDDEN,
                "Property limit reached for plan " + admin.getSubscriptionPlan() + ": " + current + "/" + limit);
        }

        Property property = propertyMapper.toEntity(request);
        property.setAdmin(admin);

        Property savedProperty = propertyRepository.save(property);
        log.info("Property created: {} for admin: {}", savedProperty.getName(), adminId);

        return propertyMapper.toResponse(savedProperty);
    }

    @Transactional
    public PropertyResponse updateProperty(UUID propertyId, PropertyUpdateRequest request) {
        Property property = propertyRepository.findById(propertyId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Property not found: " + propertyId));

        propertyMapper.updateEntity(property, request);
        return propertyMapper.toResponse(propertyRepository.save(property));
    }

    public PropertyResponse getPropertyById(UUID propertyId) {
        return propertyMapper.toResponse(
            propertyRepository.findById(propertyId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Property not found: " + propertyId))
        );
    }

    public PropertyDetailResponse getPropertyDetail(UUID propertyId) {
        return propertyMapper.toDetailResponse(
            propertyRepository.findById(propertyId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Property not found: " + propertyId))
        );
    }

    public List<PropertyResponse> getPropertiesByAdmin(UUID adminId) {
        if (!adminRepository.existsById(adminId))
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Admin not found: " + adminId);
        return propertyMapper.toResponseList(propertyRepository.findActiveByAdmin(adminId));
    }

    public Page<PropertyResponse> getPropertiesByAdmin(UUID adminId, Pageable pageable) {
        if (!adminRepository.existsById(adminId))
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Admin not found: " + adminId);
        return propertyRepository.findByAdmin_UserId(adminId, pageable)
            .map(propertyMapper::toResponse);
    }

    public List<PropertyResponse> getAllProperties() {
        return propertyMapper.toResponseList(propertyRepository.findAll());
    }

    public Page<PropertyResponse> getAllProperties(Pageable pageable) {
        return propertyRepository.findAll(pageable).map(propertyMapper::toResponse);
    }

    @Transactional
    public void deleteProperty(UUID propertyId) {
        if (!propertyRepository.existsById(propertyId))
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Property not found: " + propertyId);
        propertyRepository.deleteById(propertyId);
        log.info("Property deleted: {}", propertyId);
    }

    @Transactional
    public void softDeleteProperty(UUID propertyId) {
        if (!propertyRepository.existsById(propertyId))
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Property not found: " + propertyId);
        propertyRepository.softDelete(propertyId, OffsetDateTime.now());
        log.info("Property soft deleted: {}", propertyId);
    }

    @Transactional
    public void activateProperty(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Property not found: " + propertyId));
        property.setStatus(PropertyStatus.ACTIVE);
    }

    @Transactional
    public void deactivateProperty(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Property not found: " + propertyId));
        property.setStatus(PropertyStatus.INACTIVE);
    }

    public long countPropertiesByAdmin(UUID adminId) {
        return propertyRepository.countActiveByAdmin(adminId);
    }

    public boolean canAddMoreProperties(UUID adminId) {
        return subscriptionService.canAddProperty(adminId);
    }
}
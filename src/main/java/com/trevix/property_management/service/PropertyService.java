package com.trevix.property_management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.trevix.property_management.dto.request.PropertyCreateRequest;
import com.trevix.property_management.dto.request.PropertyUpdateRequest;
import com.trevix.property_management.dto.response.PropertyDetailResponse;
import com.trevix.property_management.dto.response.PropertyResponse;
import com.trevix.property_management.entity.Property;
import com.trevix.property_management.entity.User;
import com.trevix.property_management.enums.ErrorCode;
import com.trevix.property_management.enums.PropertyStatus;
import com.trevix.property_management.exception.AppException;
import com.trevix.property_management.mapper.PropertyMapper;
import com.trevix.property_management.repository.PropertyRepository;
import com.trevix.property_management.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final PropertyMapper propertyMapper;

    @Transactional
    public PropertyResponse createProperty(UUID ownerId, PropertyCreateRequest request) {
        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User not found: " + ownerId));

        Property property = propertyMapper.toEntity(request);
        property.setOwner(owner);

        Property saved = propertyRepository.save(property);
        log.info("Property created: {} for owner: {}", saved.getName(), ownerId);

        return propertyMapper.toResponse(saved);
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

    public List<PropertyResponse> getPropertiesByOwner(UUID ownerId) {
        return propertyMapper.toResponseList(propertyRepository.findActiveByOwner(ownerId));
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
}
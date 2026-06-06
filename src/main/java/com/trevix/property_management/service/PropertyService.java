package com.trevix.property_management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.trevix.property_management.dto.request.PropertyCreateRequest;
import com.trevix.property_management.dto.request.PropertyUpdateRequest;
import com.trevix.property_management.dto.response.PropertyResponse;
import com.trevix.property_management.dto.response.PropertyDetailResponse;
import com.trevix.property_management.entity.Admin;
import com.trevix.property_management.entity.Property;
import com.trevix.property_management.enums.PropertyStatus;
import com.trevix.property_management.exception.*;
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
            .orElseThrow(() -> new ResourceNotFoundException("Admin", adminId));
        
        if (!subscriptionService.canAddProperty(adminId)) {
            throw new SubscriptionLimitExceededException(
                admin.getSubscriptionPlan().toString(), 
                subscriptionService.getPropertyLimit(admin.getSubscriptionPlan()),
                propertyRepository.countActiveByAdmin(adminId)
            );
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
            .orElseThrow(() -> new ResourceNotFoundException("Property", propertyId));
        
        propertyMapper.updateEntity(property, request);
        Property updatedProperty = propertyRepository.save(property);
        
        return propertyMapper.toResponse(updatedProperty);
    }
    
    public PropertyResponse getPropertyById(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId)
            .orElseThrow(() -> new ResourceNotFoundException("Property", propertyId));
        return propertyMapper.toResponse(property);
    }
    
    public PropertyDetailResponse getPropertyDetail(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId)
            .orElseThrow(() -> new ResourceNotFoundException("Property", propertyId));
        return propertyMapper.toDetailResponse(property);
    }
    
    public List<PropertyResponse> getPropertiesByAdmin(UUID adminId) {
        if (!adminRepository.existsById(adminId)) {
            throw new ResourceNotFoundException("Admin", adminId);
        }
        return propertyMapper.toResponseList(propertyRepository.findActiveByAdmin(adminId));
    }
    
    public Page<PropertyResponse> getPropertiesByAdmin(UUID adminId, Pageable pageable) {
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
        if (!propertyRepository.existsById(propertyId)) {
            throw new ResourceNotFoundException("Property", propertyId);
        }
        propertyRepository.deleteById(propertyId);
        log.info("Property deleted: {}", propertyId);
    }
    
    @Transactional
    public void softDeleteProperty(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId)
            .orElseThrow(() -> new ResourceNotFoundException("Property", propertyId));
        propertyRepository.softDelete(propertyId, OffsetDateTime.now());
        log.info("Property soft deleted: {}", propertyId);
    }
    
    @Transactional
    public void activateProperty(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId)
            .orElseThrow(() -> new ResourceNotFoundException("Property", propertyId));
        property.setStatus(PropertyStatus.ACTIVE);
        propertyRepository.save(property);
    }
    
    @Transactional
    public void deactivateProperty(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId)
            .orElseThrow(() -> new ResourceNotFoundException("Property", propertyId));
        property.setStatus(PropertyStatus.INACTIVE);
        propertyRepository.save(property);
    }
    
    public long countPropertiesByAdmin(UUID adminId) {
        return propertyRepository.countActiveByAdmin(adminId);
    }
    
    public boolean canAddMoreProperties(UUID adminId) {
        return subscriptionService.canAddProperty(adminId);
    }
}
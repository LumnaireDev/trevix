package com.trevix.property_management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.trevix.property_management.dto.request.TenantCreateRequest;
import com.trevix.property_management.dto.request.TenantUpdateRequest;
import com.trevix.property_management.dto.response.TenantResponse;
import com.trevix.property_management.dto.response.TenantDetailResponse;
import com.trevix.property_management.entity.Tenant;
import com.trevix.property_management.entity.User;
import com.trevix.property_management.enums.ErrorCode;
import com.trevix.property_management.enums.UserRole;
import com.trevix.property_management.exception.AppException;
import com.trevix.property_management.mapper.TenantMapper;
import com.trevix.property_management.repository.TenantRepository;
import com.trevix.property_management.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TenantService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final TenantMapper tenantMapper;

    @Transactional
    public TenantResponse createTenant(TenantCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Tenant already exists"));

        if (tenantRepository.findByUserId(request.getUserId()).isPresent())
            throw new AppException(ErrorCode.DUPLICATE_RESOURCE, "User is already registered as a tenant");

        user.setRole(UserRole.TENANT);
        userRepository.save(user);

        Tenant tenant = tenantMapper.toEntity(request);
        tenant.setUserId(user.getId());

        Tenant savedTenant = tenantRepository.save(tenant);
        log.info("Tenant created for user: {}", user.getEmail());

        return tenantMapper.toResponse(savedTenant);
    }

    @Transactional
    public TenantResponse updateTenant(UUID tenantId, TenantUpdateRequest request) {
        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Tenant not found with id: " + tenantId));

        tenantMapper.updateEntity(tenant, request);

        return tenantMapper.toResponse(tenantRepository.save(tenant));
    }

    public TenantResponse getTenantById(UUID tenantId) {
        return tenantMapper.toResponse(
            tenantRepository.findById(tenantId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Tenant not found with id: " + tenantId))
        );
    }

    public TenantDetailResponse getTenantDetail(UUID tenantId) {
        return tenantMapper.toDetailResponse(
            tenantRepository.findById(tenantId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Tenant not found with id: " + tenantId))
        );
    }

    public List<TenantResponse> getAllTenants() {
        return tenantMapper.toResponseList(tenantRepository.findAll());
    }

    public Page<TenantResponse> getAllTenants(Pageable pageable) {
        return tenantRepository.findAll(pageable).map(tenantMapper::toResponse);
    }

    public List<TenantResponse> getTenantsByProperty(UUID propertyId) {
        return tenantMapper.toResponseList(tenantRepository.findByPropertyId(propertyId));
    }

    public List<TenantResponse> getActiveTenantsByProperty(UUID propertyId) {
        return tenantMapper.toResponseList(
            tenantRepository.findByPropertyId(propertyId).stream()
                .filter(Tenant::getIsActive)
                .toList()
        );
    }

    @Transactional
    public void deleteTenant(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Tenant not found with id: " + tenantId));

        User user = tenant.getUser();
        if (user != null) {
            user.setRole(null);
            userRepository.save(user);
        }

        tenantRepository.delete(tenant);
        log.info("Tenant deleted: {}", tenantId);
    }

    @Transactional
    public void activateTenant(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Tenant not found with id: " + tenantId));

        tenant.setIsActive(true);
        if (tenant.getUser() != null) {
            tenant.getUser().setIsActive(true);
            userRepository.save(tenant.getUser());
        }
        tenantRepository.save(tenant);
    }

    @Transactional
    public void deactivateTenant(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Tenant not found with id: " + tenantId));

        tenant.setIsActive(false);
        if (tenant.getUser() != null) {
            tenant.getUser().setIsActive(false);
            userRepository.save(tenant.getUser());
        }
        tenantRepository.save(tenant);
    }

    public long countTenantsByProperty(UUID propertyId) {
        return tenantRepository.countActiveByProperty(propertyId);
    }

    @Transactional
    public void updateScorecard(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Tenant not found with id: " + tenantId));

        BigDecimal score = calculateScorecard(tenant);
        tenant.setScorecardScore(score);
        tenant.setScorecardGrade(calculateGrade(score));
        tenantRepository.save(tenant);
    }

    private BigDecimal calculateScorecard(Tenant tenant) {
        return BigDecimal.valueOf(75.0);
    }

    private String calculateGrade(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(90)) >= 0) return "A";
        if (score.compareTo(BigDecimal.valueOf(80)) >= 0) return "B";
        if (score.compareTo(BigDecimal.valueOf(70)) >= 0) return "C";
        if (score.compareTo(BigDecimal.valueOf(60)) >= 0) return "D";
        return "F";
    }
}
package com.trevix.property_management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.trevix.property_management.repository.PropertyRepository;
import com.trevix.property_management.repository.TenantRepository;
import com.trevix.property_management.repository.UserRepository;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TenantService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final TenantMapper tenantMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TenantResponse createTenant(TenantCreateRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent())
            throw new AppException(ErrorCode.DUPLICATE_RESOURCE, "Email already in use");

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(UserRole.TENANT);
        user.setIsActive(true);
        userRepository.save(user);

        Tenant tenant = tenantMapper.toEntity(request);
        tenant.setUser(user);
        Tenant saved = tenantRepository.save(tenant);

        log.info("Tenant created: {}", user.getEmail());
        return tenantMapper.toResponse(saved);
    }

    @Transactional
    public TenantResponse updateTenant(UUID tenantId, TenantUpdateRequest request) {
        Tenant tenant = findTenantById(tenantId);
        tenantMapper.updateEntity(tenant, request);
        if (tenant.getUser() != null) userRepository.save(tenant.getUser());
        return tenantMapper.toResponse(tenantRepository.save(tenant));
    }

    public TenantResponse getTenantById(UUID tenantId) {
        return tenantMapper.toResponse(findTenantById(tenantId));
    }

    public TenantDetailResponse getTenantDetail(UUID tenantId) {
        return tenantMapper.toDetailResponse(findTenantById(tenantId));
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
        Tenant tenant = findTenantById(tenantId);
        tenantRepository.delete(tenant);
        log.info("Tenant deleted: {}", tenantId);
    }

    @Transactional
    public void activateTenant(UUID tenantId) {
        Tenant tenant = findTenantById(tenantId);
        tenant.setIsActive(true);
        if (tenant.getUser() != null) {
            tenant.getUser().setIsActive(true);
            userRepository.save(tenant.getUser());
        }
        tenantRepository.save(tenant);
    }

    @Transactional
    public void deactivateTenant(UUID tenantId) {
        Tenant tenant = findTenantById(tenantId);
        tenant.setIsActive(false);
        if (tenant.getUser() != null) {
            tenant.getUser().setIsActive(false);
            userRepository.save(tenant.getUser());
        }
        tenantRepository.save(tenant);
    }

    public boolean isTenantManagedByOwner(UUID tenantId, UUID ownerId) {
        return propertyRepository.findActiveByOwner(ownerId).stream()
            .flatMap(p -> p.getRooms().stream())
            .flatMap(r -> r.getBills().stream())
            .anyMatch(b -> b.getTenant() != null &&
                          b.getTenant().getUserId().equals(tenantId));
    }

    public long countTenantsByProperty(UUID propertyId) {
        return tenantRepository.countActiveByProperty(propertyId);
    }

    private Tenant findTenantById(UUID tenantId) {
        return tenantRepository.findById(tenantId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Tenant not found: " + tenantId));
    }
}
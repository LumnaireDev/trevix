package com.trevix.property_management.mapper;

import org.springframework.stereotype.Component;
import com.trevix.property_management.dto.request.TenantCreateRequest;
import com.trevix.property_management.dto.request.TenantUpdateRequest;
import com.trevix.property_management.dto.response.TenantDetailResponse;
import com.trevix.property_management.dto.response.TenantResponse;
import com.trevix.property_management.entity.Tenant;
import com.trevix.property_management.entity.User;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TenantMapper {

    public Tenant toEntity(TenantCreateRequest request) {
        if (request == null) return null;

        Tenant tenant = new Tenant();
        tenant.setEmergencyContactName(request.getEmergencyContactName());
        tenant.setEmergencyContactPhone(request.getEmergencyContactPhone());
        tenant.setEmergencyContactRelation(request.getEmergencyContactRelation());
        tenant.setMoveInDate(request.getMoveInDate() != null ? request.getMoveInDate() : LocalDate.now());
        tenant.setIsActive(true);
        return tenant;
    }

    public void updateEntity(Tenant tenant, TenantUpdateRequest request) {
        if (request == null || tenant == null) return;

        if (request.getEmergencyContactName() != null)
            tenant.setEmergencyContactName(request.getEmergencyContactName());
        if (request.getEmergencyContactPhone() != null)
            tenant.setEmergencyContactPhone(request.getEmergencyContactPhone());
        if (request.getEmergencyContactRelation() != null)
            tenant.setEmergencyContactRelation(request.getEmergencyContactRelation());
        if (request.getMoveInDate() != null)
            tenant.setMoveInDate(request.getMoveInDate());
        if (request.getIsActive() != null)
            tenant.setIsActive(request.getIsActive());

        // Update user fields if present
        if (tenant.getUser() != null) {
            if (request.getFullName() != null)
                tenant.getUser().setFullName(request.getFullName());
            if (request.getPhone() != null)
                tenant.getUser().setPhone(request.getPhone());
        }
    }

    public TenantResponse toResponse(Tenant tenant) {
        if (tenant == null) return null;

        User user = tenant.getUser();
        return TenantResponse.builder()
            .userId(tenant.getUserId())
            .fullName(user != null ? user.getFullName() : null)
            .email(user != null ? user.getEmail() : null)
            .phone(user != null ? user.getPhone() : null)
            .emergencyContactName(tenant.getEmergencyContactName())
            .emergencyContactPhone(tenant.getEmergencyContactPhone())
            .emergencyContactRelation(tenant.getEmergencyContactRelation())
            .moveInDate(tenant.getMoveInDate())
            .isActive(tenant.getIsActive())
            .build();
    }

    public TenantDetailResponse toDetailResponse(Tenant tenant) {
        if (tenant == null) return null;

        User user = tenant.getUser();
        return TenantDetailResponse.builder()
            .userId(tenant.getUserId())
            .fullName(user != null ? user.getFullName() : null)
            .email(user != null ? user.getEmail() : null)
            .phone(user != null ? user.getPhone() : null)
            .emergencyContactName(tenant.getEmergencyContactName())
            .emergencyContactPhone(tenant.getEmergencyContactPhone())
            .emergencyContactRelation(tenant.getEmergencyContactRelation())
            .moveInDate(tenant.getMoveInDate())
            .isActive(tenant.getIsActive())
            .build();
    }

    public List<TenantResponse> toResponseList(List<Tenant> tenants) {
        if (tenants == null) return null;
        return tenants.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
package com.trevix.property_management.mapper;

import org.springframework.stereotype.Component;
import com.trevix.property_management.dto.request.TenantCreateRequest;
import com.trevix.property_management.dto.request.TenantUpdateRequest;
import com.trevix.property_management.dto.response.TenantDetailResponse;
import com.trevix.property_management.dto.response.TenantResponse;
import com.trevix.property_management.entity.Tenant;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TenantMapper {
    
    private final UserMapper userMapper;
    
    public TenantMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    
    public Tenant toEntity(TenantCreateRequest request) {
        if (request == null) {
            return null;
        }
        
        Tenant tenant = new Tenant();
        tenant.setEmergencyContactName(request.getEmergencyContactName());
        tenant.setEmergencyContactPhone(request.getEmergencyContactPhone());
        tenant.setEmergencyContactRelation(request.getEmergencyContactRelation());
        tenant.setScorecardScore(BigDecimal.ZERO);
        tenant.setMoveInDate(LocalDate.now());
        tenant.setIsActive(true);
        return tenant;
    }
    
    public void updateEntity(Tenant tenant, TenantUpdateRequest request) {
        if (request == null || tenant == null) {
            return;
        }
        
        if (request.getEmergencyContactName() != null) {
            tenant.setEmergencyContactName(request.getEmergencyContactName());
        }
        if (request.getEmergencyContactPhone() != null) {
            tenant.setEmergencyContactPhone(request.getEmergencyContactPhone());
        }
        if (request.getEmergencyContactRelation() != null) {
            tenant.setEmergencyContactRelation(request.getEmergencyContactRelation());
        }
        if (request.getIsActive() != null) {
            tenant.setIsActive(request.getIsActive());
        }
    }
    
    public TenantResponse toResponse(Tenant tenant) {
        if (tenant == null) {
            return null;
        }
        
        TenantResponse response = new TenantResponse();
        response.setUser(userMapper.toResponse(tenant.getUser()));
        response.setEmergencyContactName(tenant.getEmergencyContactName());
        response.setEmergencyContactPhone(tenant.getEmergencyContactPhone());
        response.setEmergencyContactRelation(tenant.getEmergencyContactRelation());
        response.setScorecardScore(tenant.getScorecardScore());
        response.setScorecardGrade(tenant.getScorecardGrade());
        response.setMoveInDate(tenant.getMoveInDate());
        response.setIsActive(tenant.getIsActive());
        return response;
    }
    
    public TenantDetailResponse toDetailResponse(Tenant tenant) {
        if (tenant == null) return null;
        
        TenantDetailResponse response = new TenantDetailResponse();
        response.setUserId(tenant.getUserId());
        response.setEmergencyContactName(tenant.getEmergencyContactName());
        response.setEmergencyContactPhone(tenant.getEmergencyContactPhone());
        response.setEmergencyContactRelation(tenant.getEmergencyContactRelation());
        response.setScorecardScore(tenant.getScorecardScore());
        response.setScorecardGrade(tenant.getScorecardGrade());
        response.setMoveInDate(tenant.getMoveInDate());
        response.setIsActive(tenant.getIsActive());

        // map nested relations if TenantDetailResponse has them
        // response.setLeases(leaseMapper.toResponseList(tenant.getLeases()));

        return response;
    }
    
    public List<TenantResponse> toResponseList(List<Tenant> tenants) {
        if (tenants == null) {
            return null;
        }
        return tenants.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
}
package com.trevix.property_management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.trevix.property_management.entity.Admin;
import com.trevix.property_management.enums.ErrorCode;
import com.trevix.property_management.enums.SubscriptionPlan;
import com.trevix.property_management.enums.SubscriptionStatus;
import com.trevix.property_management.exception.AppException;
import com.trevix.property_management.repository.AdminRepository;
import com.trevix.property_management.repository.PropertyRepository;
import com.trevix.property_management.repository.RoomRepository;
import com.trevix.property_management.repository.TenantRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private static final int FREE_TRIAL_MAX_PROPERTIES = 1;
    private static final int FREE_TRIAL_MAX_ROOMS = 10;
    private static final int FREE_TRIAL_MAX_TENANTS = 10;

    private static final int BASIC_MAX_PROPERTIES = 3;
    private static final int BASIC_MAX_ROOMS = 50;
    private static final int BASIC_MAX_TENANTS = 100;

    private static final int UNLIMITED = Integer.MAX_VALUE;

    private final AdminRepository adminRepository;
    private final PropertyRepository propertyRepository;
    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;

    public int getPropertyLimit(SubscriptionPlan plan) {
        return switch (plan) {
            case FREE_TRIAL -> FREE_TRIAL_MAX_PROPERTIES;
            case BASIC      -> BASIC_MAX_PROPERTIES;
            case PREMIUM    -> UNLIMITED;
        };
    }

    public int getRoomLimit(SubscriptionPlan plan) {
        return switch (plan) {
            case FREE_TRIAL -> FREE_TRIAL_MAX_ROOMS;
            case BASIC      -> BASIC_MAX_ROOMS;
            case PREMIUM    -> UNLIMITED;
        };
    }

    public int getTenantLimit(SubscriptionPlan plan) {
        return switch (plan) {
            case FREE_TRIAL -> FREE_TRIAL_MAX_TENANTS;
            case BASIC      -> BASIC_MAX_TENANTS;
            case PREMIUM    -> UNLIMITED;
        };
    }

    public boolean canAddProperty(UUID adminId) {
        long current = propertyRepository.countActiveByOwner(adminId);
        return current < getPropertyLimit(SubscriptionPlan.BASIC);
    }

    public boolean canAddRoom(UUID adminId) {
        Admin admin = getAdmin(adminId);
        assertSubscriptionActive(admin);
        long current = roomRepository.countByOwnerId(adminId);
        return current < getRoomLimit(admin.getSubscriptionPlan());
    }

    public boolean canAddTenant(UUID ownerId) {
        long current = tenantRepository.countActiveByOwner(ownerId);
        return current < getTenantLimit(SubscriptionPlan.BASIC);
    }

    @Transactional
    public void upgradePlan(UUID adminId, SubscriptionPlan newPlan) {
        Admin admin = getAdmin(adminId);
        SubscriptionPlan oldPlan = admin.getSubscriptionPlan();
        admin.setSubscriptionPlan(newPlan);
        admin.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
        adminRepository.save(admin);
        log.info("Admin {} upgraded from {} to {}", adminId, oldPlan, newPlan);
    }

    @Transactional
    public void cancelSubscription(UUID adminId) {
        Admin admin = getAdmin(adminId);
        admin.setSubscriptionStatus(SubscriptionStatus.CANCELLED);
        admin.setAutoRenew(false);
        adminRepository.save(admin);
        log.info("Subscription cancelled for admin: {}", adminId);
    }

    @Transactional
    public void suspendSubscription(UUID adminId) {
        Admin admin = getAdmin(adminId);
        admin.setSubscriptionStatus(SubscriptionStatus.SUSPENDED);
        adminRepository.save(admin);
        log.info("Subscription suspended for admin: {}", adminId);
    }

    @Transactional
    public void reactivateSubscription(UUID adminId) {
        Admin admin = getAdmin(adminId);
        admin.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
        adminRepository.save(admin);
        log.info("Subscription reactivated for admin: {}", adminId);
    }

    @Transactional
    public void expireSubscription(UUID adminId) {
        Admin admin = getAdmin(adminId);
        admin.setSubscriptionStatus(SubscriptionStatus.EXPIRED);
        admin.setAutoRenew(false);
        adminRepository.save(admin);
        log.info("Subscription expired for admin: {}", adminId);
    }

    public boolean isSubscriptionActive(UUID adminId) {
        Admin admin = getAdmin(adminId);
        return isActive(admin);
    }

    public boolean isTrialExpired(UUID adminId) {
        Admin admin = getAdmin(adminId);
        return admin.getSubscriptionPlan() == SubscriptionPlan.FREE_TRIAL
            && admin.getTrialEndDate() != null
            && admin.getTrialEndDate().isBefore(OffsetDateTime.now());
    }

    public SubscriptionPlan getCurrentPlan(UUID adminId) {
        return getAdmin(adminId).getSubscriptionPlan();
    }

    public SubscriptionStatus getCurrentStatus(UUID adminId) {
        return getAdmin(adminId).getSubscriptionStatus();
    }

    private Admin getAdmin(UUID adminId) {
        return adminRepository.findById(adminId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Admin not found: " + adminId));
    }

    private boolean isActive(Admin admin) {
        SubscriptionStatus status = admin.getSubscriptionStatus();
        return status == SubscriptionStatus.ACTIVE || status == SubscriptionStatus.TRIAL;
    }

    private void assertSubscriptionActive(Admin admin) {
        if (!isActive(admin))
            throw new AppException(ErrorCode.FORBIDDEN,
                "Subscription is " + admin.getSubscriptionStatus() + ". Please renew your plan.");
    }
}
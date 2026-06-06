package com.trevix.property_management.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;
import com.trevix.property_management.enums.SubscriptionPlan;
import com.trevix.property_management.enums.SubscriptionStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminResponse {
    private UUID userId;
    private UserResponse user;
    private String companyName;
    private String taxId;
    private SubscriptionPlan subscriptionPlan;
    private SubscriptionStatus subscriptionStatus;
    private OffsetDateTime trialEndDate;
    private OffsetDateTime currentPeriodEnd;
    private Boolean autoRenew;
}
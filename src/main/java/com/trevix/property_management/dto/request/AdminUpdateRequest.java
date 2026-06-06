package com.trevix.property_management.dto.request;

import java.time.OffsetDateTime;
import com.trevix.property_management.enums.SubscriptionPlan;
import com.trevix.property_management.enums.SubscriptionStatus;
import lombok.Data;

@Data
public class AdminUpdateRequest {
    private String companyName;
    private String taxId;
    private SubscriptionPlan subscriptionPlan;
    private SubscriptionStatus subscriptionStatus;
    private OffsetDateTime trialEndDate;
    private OffsetDateTime currentPeriodEnd;
    private Boolean autoRenew;
}

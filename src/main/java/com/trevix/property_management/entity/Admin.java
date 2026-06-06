package com.trevix.property_management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.OffsetDateTime;
import java.util.UUID;
import com.trevix.property_management.enums.SubscriptionPlan;
import com.trevix.property_management.enums.SubscriptionStatus;

@Entity
@Table(name = "admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    
    @Id
    @Column(name = "user_id")
    private UUID userId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "company_name")
    private String companyName;
    
    @Column(name = "tax_id")
    private String taxId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_plan", nullable = false)
    private SubscriptionPlan subscriptionPlan = SubscriptionPlan.FREE_TRIAL;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status", nullable = false)
    private SubscriptionStatus subscriptionStatus = SubscriptionStatus.TRIAL;
    
    @Column(name = "trial_end_date")
    private OffsetDateTime trialEndDate;
    
    @Column(name = "current_period_end")
    private OffsetDateTime currentPeriodEnd;
    
    @Column(name = "auto_renew")
    private Boolean autoRenew = false;
}
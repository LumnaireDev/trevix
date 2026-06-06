package com.trevix.property_management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.trevix.property_management.enums.VisitorStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "visitors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Visitor {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    
    @Column(name = "visitor_name", nullable = false)
    private String visitorName;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "id_number")
    private String idNumber;
    
    @Column(name = "planned_arrival")
    private OffsetDateTime plannedArrival;
    
    @Column(name = "planned_departure")
    private OffsetDateTime plannedDeparture;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VisitorStatus status = VisitorStatus.PENDING;
    
    @Column(name = "actual_checkin")
    private OffsetDateTime actualCheckin;
    
    @Column(name = "actual_checkout")
    private OffsetDateTime actualCheckout;
    
    @Column(name = "temporary_pass_issued")
    private Boolean temporaryPassIssued = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registered_by")
    private User registeredBy;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
}
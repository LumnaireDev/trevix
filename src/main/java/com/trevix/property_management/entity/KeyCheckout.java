package com.trevix.property_management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "key_checkouts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class KeyCheckout {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
    
    @Column(name = "key_description", nullable = false)
    private String keyDescription;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checked_out_to")
    private Tenant checkedOutTo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checked_out_to_visitor")
    private Visitor checkedOutToVisitor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checked_out_by", nullable = false)
    private StaffProfile checkedOutBy;
    
    @CreatedDate
    @Column(name = "checkout_time", updatable = false)
    private OffsetDateTime checkoutTime;
    
    @Column(name = "expected_return_time")
    private OffsetDateTime expectedReturnTime;
    
    @Column(name = "return_time")
    private OffsetDateTime returnTime;
    
    @Column(name = "is_returned")
    private Boolean isReturned = false;
    
    @Column(name = "notes")
    private String notes;
}
package com.trevix.property_management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import com.trevix.property_management.enums.ModuleType;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "property_modules",
       uniqueConstraints = @UniqueConstraint(columnNames = {"property_id", "module_type"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyModule {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "module_type", nullable = false)
    private ModuleType moduleType;
    
    @Column(name = "activation_key_hash")
    private String activationKeyHash;
    
    @Column(name = "is_active")
    private Boolean isActive = false;
    
    @Column(name = "activated_at")
    private OffsetDateTime activatedAt;
    
    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;
    
    @Column(name = "purchased_at")
    private OffsetDateTime purchasedAt;
}
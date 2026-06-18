package com.trevix.property_management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.trevix.property_management.enums.UserRole;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Column(name = "full_name", nullable = false)
    private String fullName;
    
    @Column(name = "phone")
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Tenant tenant;
}

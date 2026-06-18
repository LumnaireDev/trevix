package com.trevix.property_management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.trevix.property_management.enums.PropertyStatus;
import com.trevix.property_management.enums.PropertyType;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Property {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;                         // ← was Admin, now User directly

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private PropertyType type;                  // DORMITORY, APARTMENT, CONDOMINIUM

    @Column(name = "timezone")
    private String timezone = "Asia/Manila";

    @Column(name = "room_rules", columnDefinition = "TEXT")
    private String roomRules;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PropertyStatus status = PropertyStatus.ACTIVE;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Room> rooms = new ArrayList<>();
}
package com.trevix.property_management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tenants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {
    
    @Id
    @Column(name = "user_id")
    private UUID userId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "emergency_contact_name")
    private String emergencyContactName;
    
    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;
    
    @Column(name = "emergency_contact_relation")
    private String emergencyContactRelation;
    
    @Column(name = "scorecard_score", precision = 3, scale = 2)
    private BigDecimal scorecardScore = BigDecimal.ZERO;
    
    @Column(name = "scorecard_grade", length = 1)
    private String scorecardGrade;
    
    @Column(name = "move_in_date")
    private LocalDate moveInDate;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
}
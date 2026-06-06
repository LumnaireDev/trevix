package com.trevix.property_management.repository;

import com.trevix.property_management.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, UUID> {
    
    List<Announcement> findByPropertyId(UUID propertyId);
    
    List<Announcement> findByAdmin_UserId(UUID adminId);
    
    @Query("SELECT a FROM Announcement a WHERE a.property.id = :propertyId ORDER BY a.createdAt DESC")
    Page<Announcement> findByPropertyIdOrderByCreatedAtDesc(@Param("propertyId") UUID propertyId, Pageable pageable);
    
    @Query("SELECT a FROM Announcement a WHERE a.property.id = :propertyId AND a.targetType = 'all'")
    List<Announcement> findGlobalAnnouncements(@Param("propertyId") UUID propertyId);
    
    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.property.id = :propertyId AND a.createdAt >= :startDate")
    long countAnnouncementsSince(@Param("propertyId") UUID propertyId, @Param("startDate") java.time.OffsetDateTime startDate);
}
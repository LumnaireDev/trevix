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

    List<Announcement> findByProperty_IdOrderByCreatedAtDesc(UUID propertyId);

    @Query("SELECT a FROM Announcement a WHERE a.property.owner.id = :ownerId ORDER BY a.createdAt DESC")
    List<Announcement> findByOwnerId(@Param("ownerId") UUID ownerId);

    @Query("SELECT a FROM Announcement a WHERE a.property.owner.id = :ownerId ORDER BY a.createdAt DESC")
    Page<Announcement> findRecentByOwner(@Param("ownerId") UUID ownerId, Pageable pageable);
}
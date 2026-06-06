package com.trevix.property_management.repository;

import com.trevix.property_management.entity.AnnouncementReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Repository
public interface AnnouncementReactionRepository extends JpaRepository<AnnouncementReaction, UUID> {
    
    List<AnnouncementReaction> findByAnnouncementId(UUID announcementId);
    
    List<AnnouncementReaction> findByTenant_UserId(UUID tenantId);
    
    @Query("SELECT ar.reactionType, COUNT(ar) FROM AnnouncementReaction ar WHERE ar.announcement.id = :announcementId GROUP BY ar.reactionType")
    List<Object[]> countReactionsByType(@Param("announcementId") UUID announcementId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM AnnouncementReaction ar WHERE ar.announcement.id = :announcementId AND ar.tenant.userId = :tenantId AND ar.reactionType = :reactionType")
    void deleteReaction(@Param("announcementId") UUID announcementId, 
                        @Param("tenantId") UUID tenantId, 
                        @Param("reactionType") String reactionType);
}
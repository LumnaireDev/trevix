package com.trevix.property_management.repository;

import com.trevix.property_management.entity.AnnouncementReadReceipt;
import com.trevix.property_management.entity.AnnouncementReadReceiptId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface AnnouncementReadReceiptRepository extends JpaRepository<AnnouncementReadReceipt, AnnouncementReadReceiptId> {
    
    List<AnnouncementReadReceipt> findByAnnouncementId(UUID announcementId);
    
    List<AnnouncementReadReceipt> findByTenant_UserId(UUID tenantId);
    
    @Query("SELECT COUNT(arr) FROM AnnouncementReadReceipt arr WHERE arr.announcement.id = :announcementId")
    long countReadReceipts(@Param("announcementId") UUID announcementId);
    
    @Query("SELECT CASE WHEN COUNT(arr) > 0 THEN true ELSE false END FROM AnnouncementReadReceipt arr WHERE arr.announcement.id = :announcementId AND arr.tenant.userId = :tenantId")
    boolean hasTenantRead(@Param("announcementId") UUID announcementId, @Param("tenantId") UUID tenantId);
}
package com.trevix.property_management.repository;

import com.trevix.property_management.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    
    List<Notification> findByUser_Id(UUID userId);
    
    Page<Notification> findByUser_IdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    List<Notification> findByUser_IdAndIsReadFalse(UUID userId);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    long countUnreadByUser(@Param("userId") UUID userId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.id = :notificationId")
    void markAsRead(@Param("userId") UUID userId, @Param("notificationId") UUID notificationId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId")
    void markAllAsRead(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId")
    void markAllAsReadByUserId(@Param("userId") UUID userId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.user.id = :userId AND n.isRead = true AND n.createdAt < :date")
    void deleteOldReadNotifications(@Param("userId") UUID userId, @Param("date") java.time.OffsetDateTime date);
}
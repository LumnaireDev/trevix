package com.trevix.property_management.repository;

import com.trevix.property_management.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, UUID> {
    
    List<UserDevice> findByUser_Id(UUID userId);
    
    Optional<UserDevice> findByFcmToken(String fcmToken);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM UserDevice ud WHERE ud.user.id = :userId AND ud.fcmToken = :fcmToken")
    void deleteByUserAndToken(@Param("userId") UUID userId, @Param("fcmToken") String fcmToken);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM UserDevice ud WHERE ud.user.id = :userId")
    void deleteAllByUser(@Param("userId") UUID userId);
}
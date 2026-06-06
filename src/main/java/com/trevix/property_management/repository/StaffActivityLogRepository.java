package com.trevix.property_management.repository;

import com.trevix.property_management.entity.StaffActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface StaffActivityLogRepository extends JpaRepository<StaffActivityLog, UUID> {
    
    List<StaffActivityLog> findByStaffUser_UserId(UUID staffUserId);
    
    Page<StaffActivityLog> findByStaffUser_UserIdOrderByCreatedAtDesc(UUID staffUserId, Pageable pageable);
    
    List<StaffActivityLog> findByAction(String action);
    
    @Query("SELECT sal FROM StaffActivityLog sal WHERE sal.staffUser.userId = :staffId AND sal.createdAt BETWEEN :startDate AND :endDate")
    List<StaffActivityLog> findByStaffAndDateRange(@Param("staffId") UUID staffId, 
                                                    @Param("startDate") OffsetDateTime startDate, 
                                                    @Param("endDate") OffsetDateTime endDate);
    
    @Query("SELECT sal.staffUser.userId, COUNT(sal) FROM StaffActivityLog sal WHERE sal.createdAt >= :since GROUP BY sal.staffUser.userId")
    List<Object[]> countActivitiesPerStaff(@Param("since") OffsetDateTime since);
}
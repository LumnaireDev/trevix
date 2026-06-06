package com.trevix.property_management.repository;

import com.trevix.property_management.entity.UtilityMeterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UtilityMeterReadingRepository extends JpaRepository<UtilityMeterReading, UUID> {
    
    List<UtilityMeterReading> findByRoomId(UUID roomId);
    
    Optional<UtilityMeterReading> findByRoomIdAndReadingDate(UUID roomId, LocalDate readingDate);
    
    @Query("SELECT u FROM UtilityMeterReading u WHERE u.room.id = :roomId ORDER BY u.readingDate DESC")
    List<UtilityMeterReading> findLatestByRoom(@Param("roomId") UUID roomId);
    
    @Query("SELECT u FROM UtilityMeterReading u WHERE u.room.id = :roomId AND u.readingDate BETWEEN :startDate AND :endDate")
    List<UtilityMeterReading> findByRoomAndDateRange(@Param("roomId") UUID roomId, 
                                                      @Param("startDate") LocalDate startDate, 
                                                      @Param("endDate") LocalDate endDate);
}
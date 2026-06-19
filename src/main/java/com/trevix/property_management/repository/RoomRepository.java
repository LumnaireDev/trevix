package com.trevix.property_management.repository;

import com.trevix.property_management.entity.Room;
import com.trevix.property_management.enums.RoomStatus;
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
public interface RoomRepository extends JpaRepository<Room, UUID> {
    @Query("SELECT COUNT(r) FROM Room r WHERE r.isActive = true AND r.property.owner.id = :ownerId")
    long countByOwnerId(@Param("ownerId") UUID ownerId);
    
    List<Room> findByPropertyId(UUID propertyId);
    
    Optional<Room> findByPropertyIdAndRoomNumber(UUID propertyId, String roomNumber);
    
    List<Room> findByStatus(RoomStatus status);
    
    @Query("SELECT r FROM Room r WHERE r.property.id = :propertyId AND r.status = 'AVAILABLE'")
    List<Room> findAvailableRoomsByProperty(@Param("propertyId") UUID propertyId);
    
    @Query("SELECT r FROM Room r WHERE r.property.id = :propertyId AND r.isActive = true")
    List<Room> findActiveByProperty(@Param("propertyId") UUID propertyId);
    
    @Query("SELECT COUNT(r) FROM Room r WHERE r.property.id = :propertyId AND r.status = 'OCCUPIED'")
    long countOccupiedRooms(@Param("propertyId") UUID propertyId);
    
    @Query("SELECT COUNT(r) FROM Room r WHERE r.property.id = :propertyId")
    long countTotalRooms(@Param("propertyId") UUID propertyId);
    
    @Query("SELECT SUM(r.currentOccupancy) FROM Room r WHERE r.property.id = :propertyId")
    Integer getTotalOccupancy(@Param("propertyId") UUID propertyId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Room r SET r.currentOccupancy = r.currentOccupancy + 1 WHERE r.id = :roomId")
    void incrementOccupancy(@Param("roomId") UUID roomId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Room r SET r.currentOccupancy = r.currentOccupancy - 1 WHERE r.id = :roomId")
    void decrementOccupancy(@Param("roomId") UUID roomId);
}
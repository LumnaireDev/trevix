package com.trevix.property_management.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.trevix.property_management.dto.request.RoomCreateRequest;
import com.trevix.property_management.dto.request.RoomUpdateRequest;
import com.trevix.property_management.dto.response.RoomDetailResponse;
import com.trevix.property_management.dto.response.RoomResponse;
import com.trevix.property_management.entity.Property;
import com.trevix.property_management.entity.Room;
import com.trevix.property_management.enums.ErrorCode;
import com.trevix.property_management.enums.RoomStatus;
import com.trevix.property_management.exception.AppException;
import com.trevix.property_management.mapper.RoomMapper;
import com.trevix.property_management.repository.PropertyRepository;
import com.trevix.property_management.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomService {
    private final RoomRepository roomRepository;
    private final PropertyRepository propertyRepository;
    private final RoomMapper roomMapper;

    @Transactional
    public RoomResponse createRoom(UUID propertyId, RoomCreateRequest request){
        Property property = propertyRepository.findById(propertyId)
        .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Property already exists"));

        if(roomRepository.findByPropertyIdAndRoomNumber(propertyId, request.getRoomNumber()).isPresent()){
            throw new AppException(ErrorCode.DUPLICATE_RESOURCE, "Property and room number exists");
        }

        Room room = roomMapper.toEntity(request);
        room.setProperty(property);

        Room savedRoom = roomRepository.save(room);
        log.info("Room created: {} in property: {}", savedRoom.getRoomNumber(), propertyId);
        
        return roomMapper.toResponse(savedRoom);
    }

    @Transactional
    public RoomResponse updateRoom(UUID roomId, RoomUpdateRequest request) {
        Room room = checkRoom(null, roomId);
        
        roomMapper.updateEntity(room, request);
        Room updatedRoom = roomRepository.save(room);
        
        return roomMapper.toResponse(updatedRoom);
    }

    public RoomResponse getRoomById(UUID roomId){
        Room room = checkRoom(null, roomId);
        return roomMapper.toResponse(room);
    }

    public RoomDetailResponse getRoomDetail(UUID roomId){
        return roomMapper
        .toDetailResponse(roomRepository.findById(roomId)
        .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Room detail not found")));
    }
    public List<RoomResponse> getRoomsByProperty(UUID propertyId){
        return roomMapper.toResponseList(roomRepository.findByPropertyId(propertyId));
    }

    public List<RoomResponse> getAvailableRoomsByProperty(UUID propertyId){
        return roomMapper.toResponseList(roomRepository.findAvailableRoomsByProperty(propertyId));
    }

    @Transactional
    public void deleteRoom(UUID roomId){
        if(!roomRepository.existsById(roomId)){
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Room not found");
        }
        roomRepository.deleteById(roomId);
        log.info("Room deleted: {}", roomId);
    }

    @Transactional
    public void activateRoom(UUID roomId){
        Room room = checkRoom(null, roomId);

        room.setIsActive(true);
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);
    }

    @Transactional
    public void deactivateRoom(UUID roomId){
        Room room = checkRoom(null, roomId);
        room.setIsActive(false);
        room.setStatus(RoomStatus.MAINTENANCE);
        roomRepository.save(room);
    }

    @Transactional
    public void updateOccupancy(UUID roomId, int delta){
        Room room = checkRoom(null, roomId);
        
        int newOccupancy = room.getCurrentOccupancy() + delta;
        if(newOccupancy < 0 || newOccupancy > room.getCapacity()){
            throw new AppException(ErrorCode.BAD_REQUEST, "Invalid occupancy values");
        }

        if (delta > 0) {
            roomRepository.incrementOccupancy(roomId);
        } else if (delta < 0) {
            roomRepository.decrementOccupancy(roomId);
        }
        
        room = roomRepository.findById(roomId).get();
        if (room.getCurrentOccupancy() >= room.getCapacity()) {
            room.setStatus(RoomStatus.OCCUPIED);
        } else if (room.getCurrentOccupancy() > 0) {
            room.setStatus(RoomStatus.OCCUPIED);
        } else {
            room.setStatus(RoomStatus.AVAILABLE);
        }
        roomRepository.save(room);
    }

    public boolean isRoomAvailable(UUID roomId){
        Room room = checkRoom(null, roomId);
         return room.getIsActive() && 
               room.getStatus() == RoomStatus.AVAILABLE && 
               room.getCurrentOccupancy() < room.getCapacity();
    }

    public long countOccupiedRooms(UUID propertyId){
        return roomRepository.countOccupiedRooms(propertyId);
    }

    public long countTotalRooms(UUID propertyId){
        return roomRepository.countTotalRooms(propertyId);
    }

    public int getTotalOccupancy(UUID propertyId){
        Integer occupancy = roomRepository.getTotalOccupancy(propertyId);
        return occupancy != null ? occupancy : 0;
    }

    private Room checkRoom(Room room, UUID roomId){
        return room != null ? room : roomRepository
        .findById(roomId).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Room not found"));
    }
}
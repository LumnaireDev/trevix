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
    public RoomResponse createRoom(UUID propertyId, RoomCreateRequest request) {
        Property property = propertyRepository.findById(propertyId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Property not found: " + propertyId));

        if (roomRepository.findByPropertyIdAndRoomNumber(propertyId, request.getRoomNumber()).isPresent())
            throw new AppException(ErrorCode.DUPLICATE_RESOURCE, "Room number already exists in this property");

        Room room = roomMapper.toEntity(request);
        room.setProperty(property);

        Room saved = roomRepository.save(room);
        log.info("Room created: {} in property: {}", saved.getRoomNumber(), propertyId);
        return roomMapper.toResponse(saved);
    }

    @Transactional
    public RoomResponse updateRoom(UUID roomId, RoomUpdateRequest request) {
        Room room = findRoomById(roomId);
        roomMapper.updateEntity(room, request);
        return roomMapper.toResponse(roomRepository.save(room));
    }

    public RoomResponse getRoomById(UUID roomId) {
        return roomMapper.toResponse(findRoomById(roomId));
    }

    public RoomDetailResponse getRoomDetail(UUID roomId) {
        return roomMapper.toDetailResponse(findRoomById(roomId));
    }

    public List<RoomResponse> getRoomsByProperty(UUID propertyId) {
        return roomMapper.toResponseList(roomRepository.findByPropertyId(propertyId));
    }

    public List<RoomResponse> getAvailableRoomsByProperty(UUID propertyId) {
        return roomMapper.toResponseList(roomRepository.findAvailableRoomsByProperty(propertyId));
    }

    @Transactional
    public void deleteRoom(UUID roomId) {
        if (!roomRepository.existsById(roomId))
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Room not found: " + roomId);
        roomRepository.deleteById(roomId);
        log.info("Room deleted: {}", roomId);
    }

    @Transactional
    public void activateRoom(UUID roomId) {
        Room room = findRoomById(roomId);
        room.setIsActive(true);
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);
    }

    @Transactional
    public void deactivateRoom(UUID roomId) {
        Room room = findRoomById(roomId);
        room.setIsActive(false);
        room.setStatus(RoomStatus.MAINTENANCE);
        roomRepository.save(room);
    }

    @Transactional
    public void updateOccupancy(UUID roomId, int delta) {
        Room room = findRoomById(roomId);
        int newOccupancy = room.getCurrentOccupancy() + delta;

        if (newOccupancy < 0 || newOccupancy > room.getCapacity())
            throw new AppException(ErrorCode.BAD_REQUEST, "Invalid occupancy value");

        if (delta > 0) roomRepository.incrementOccupancy(roomId);
        else if (delta < 0) roomRepository.decrementOccupancy(roomId);

        room = findRoomById(roomId);
        room.setStatus(room.getCurrentOccupancy() >= room.getCapacity()
            ? RoomStatus.OCCUPIED : RoomStatus.AVAILABLE);
        roomRepository.save(room);
    }

    public boolean isRoomAvailable(UUID roomId) {
        Room room = findRoomById(roomId);
        return room.getIsActive()
            && room.getStatus() == RoomStatus.AVAILABLE
            && room.getCurrentOccupancy() < room.getCapacity();
    }

    public long countOccupiedRooms(UUID propertyId) {
        return roomRepository.countOccupiedRooms(propertyId);
    }

    public long countTotalRooms(UUID propertyId) {
        return roomRepository.countTotalRooms(propertyId);
    }

    private Room findRoomById(UUID roomId) {
        return roomRepository.findById(roomId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Room not found: " + roomId));
    }
}
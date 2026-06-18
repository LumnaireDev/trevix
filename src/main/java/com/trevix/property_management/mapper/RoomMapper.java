package com.trevix.property_management.mapper;

import org.springframework.stereotype.Component;
import com.trevix.property_management.dto.request.RoomCreateRequest;
import com.trevix.property_management.dto.request.RoomUpdateRequest;
import com.trevix.property_management.dto.response.RoomDetailResponse;
import com.trevix.property_management.dto.response.RoomResponse;
import com.trevix.property_management.entity.Room;
import com.trevix.property_management.enums.RoomStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoomMapper {

    public Room toEntity(RoomCreateRequest request) {
        if (request == null) return null;

        Room room = new Room();
        room.setRoomNumber(request.getRoomNumber());
        room.setFloor(request.getFloor());
        room.setBaseRent(request.getBaseRent() != null ? request.getBaseRent() : BigDecimal.ZERO);
        room.setWaterFee(request.getWaterFee() != null ? request.getWaterFee() : BigDecimal.ZERO);
        room.setElectricityFee(request.getElectricityFee() != null ? request.getElectricityFee() : BigDecimal.ZERO);
        room.setCapacity(request.getCapacity() != null ? request.getCapacity() : 1);
        room.setCurrentOccupancy(0);
        room.setDescription(request.getDescription());
        room.setStatus(RoomStatus.AVAILABLE);
        room.setIsActive(true);

        return room;
    }

    public void updateEntity(Room room, RoomUpdateRequest request) {
        if (request == null || room == null) return;

        if (request.getRoomNumber() != null) room.setRoomNumber(request.getRoomNumber());
        if (request.getFloor() != null) room.setFloor(request.getFloor());
        if (request.getBaseRent() != null) room.setBaseRent(request.getBaseRent());
        if (request.getWaterFee() != null) room.setWaterFee(request.getWaterFee());
        if (request.getElectricityFee() != null) room.setElectricityFee(request.getElectricityFee());
        if (request.getCapacity() != null) room.setCapacity(request.getCapacity());
        if (request.getDescription() != null) room.setDescription(request.getDescription());
        if (request.getStatus() != null) room.setStatus(request.getStatus());
        if (request.getIsActive() != null) room.setIsActive(request.getIsActive());
    }

    public RoomResponse toResponse(Room room) {
        if (room == null) return null;

        RoomResponse response = new RoomResponse();
        response.setId(room.getId());
        response.setRoomNumber(room.getRoomNumber());
        response.setFloor(room.getFloor());
        response.setBaseRent(room.getBaseRent());
        response.setWaterFee(room.getWaterFee());
        response.setElectricityFee(room.getElectricityFee());
        response.setCapacity(room.getCapacity());
        response.setCurrentOccupancy(room.getCurrentOccupancy());
        response.setDescription(room.getDescription());
        response.setStatus(room.getStatus());
        response.setIsActive(room.getIsActive());
        response.setCreatedAt(room.getCreatedAt());
        response.setUpdatedAt(room.getUpdatedAt());

        if (room.getProperty() != null) {
            response.setPropertyId(room.getProperty().getId());
            response.setPropertyName(room.getProperty().getName());
        }

        return response;
    }

    public RoomDetailResponse toDetailResponse(Room room) {
        if (room == null) return null;

        RoomDetailResponse response = new RoomDetailResponse();
        response.setId(room.getId());
        response.setRoomNumber(room.getRoomNumber());
        response.setFloor(room.getFloor());
        response.setBaseRent(room.getBaseRent());
        response.setWaterFee(room.getWaterFee());
        response.setElectricityFee(room.getElectricityFee());
        response.setCapacity(room.getCapacity());
        response.setCurrentOccupancy(room.getCurrentOccupancy());
        response.setDescription(room.getDescription());
        response.setStatus(room.getStatus());
        response.setIsActive(room.getIsActive());
        response.setCreatedAt(room.getCreatedAt());
        response.setUpdatedAt(room.getUpdatedAt());

        if (room.getProperty() != null) {
            response.setPropertyId(room.getProperty().getId());
            response.setPropertyName(room.getProperty().getName());
        }

        return response;
    }

    public List<RoomResponse> toResponseList(List<Room> rooms) {
        if (rooms == null) return null;
        return rooms.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
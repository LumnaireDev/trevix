package com.trevix.property_management.mapper;

import org.springframework.stereotype.Component;
import com.trevix.property_management.dto.request.PropertyCreateRequest;
import com.trevix.property_management.dto.request.PropertyUpdateRequest;
import com.trevix.property_management.dto.response.PropertyDetailResponse;
import com.trevix.property_management.dto.response.PropertyResponse;
import com.trevix.property_management.entity.Admin;
import com.trevix.property_management.entity.Property;
import com.trevix.property_management.entity.Room;
import com.trevix.property_management.entity.User;
import com.trevix.property_management.enums.PropertyStatus;
import com.trevix.property_management.enums.RoomStatus;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PropertyMapper {

    public Property toEntity(PropertyCreateRequest request) {
        if (request == null) return null;

        Property property = new Property();
        property.setName(request.getName());
        property.setAddress(request.getAddress());
        property.setTimezone(request.getTimezone() != null ? request.getTimezone() : "Asia/Manila");
        if (request.getCurfewTime() != null && !request.getCurfewTime().isEmpty()) {
            property.setCurfewTime(LocalTime.parse(request.getCurfewTime()));
        }
        property.setRoomRules(request.getRoomRules());
        property.setStatus(PropertyStatus.ACTIVE);

        return property;
    }

    public void updateEntity(Property property, PropertyUpdateRequest request) {
        if (request == null || property == null) return;

        if (request.getName() != null) property.setName(request.getName());
        if (request.getAddress() != null) property.setAddress(request.getAddress());
        if (request.getTimezone() != null) property.setTimezone(request.getTimezone());
        if (request.getCurfewTime() != null) property.setCurfewTime(request.getCurfewTime());
        if (request.getRoomRules() != null) property.setRoomRules(request.getRoomRules());
        if (request.getStatus() != null) property.setStatus(request.getStatus());
    }

    public PropertyResponse toResponse(Property property) {
        if (property == null) return null;

        Admin admin = property.getAdmin();
        User user = admin != null ? admin.getUser() : null;

        return PropertyResponse.builder()
                .propertyId(property.getId())
                .name(property.getName())
                .address(property.getAddress())
                .timezone(property.getTimezone())
                .curfewTime(property.getCurfewTime() != null ? property.getCurfewTime().toString() : null)
                .roomRules(property.getRoomRules())
                .status(property.getStatus())
                .adminId(admin != null ? admin.getUserId() : null)
                .adminName(user != null ? user.getFullName() : null)
                .createdAt(property.getCreatedAt())
                .updatedAt(property.getUpdatedAt())
                .build();
    }

    public PropertyDetailResponse toDetailResponse(Property property) {
        if (property == null) return null;

        Admin admin = property.getAdmin();
        User user = admin != null ? admin.getUser() : null;

        // Room stats
        List<Room> rooms = property.getRooms();
        int totalRooms = rooms != null ? rooms.size() : 0;

        long occupiedRooms = totalRooms == 0 ? 0 : rooms.stream()
                .filter(r -> r.getCurrentOccupancy() != null && r.getCurrentOccupancy() > 0)
                .count();

        long availableRooms = totalRooms == 0 ? 0 : rooms.stream()
                .filter(r -> r.getStatus() == RoomStatus.AVAILABLE)
                .count();

        int totalOccupancy = totalRooms == 0 ? 0 : rooms.stream()
                .mapToInt(r -> r.getCurrentOccupancy() != null ? r.getCurrentOccupancy() : 0)
                .sum();

        double occupancyRate = totalRooms > 0
                ? Math.round(((double) occupiedRooms / totalRooms) * 1000.0) / 10.0
                : 0.0;

        return PropertyDetailResponse.builder()
                .propertyId(property.getId())
                .name(property.getName())
                .address(property.getAddress())
                .timezone(property.getTimezone())
                .curfewTime(property.getCurfewTime() != null ? property.getCurfewTime().toString() : null)
                .roomRules(property.getRoomRules())
                .status(property.getStatus())
                .adminId(admin != null ? admin.getUserId() : null)
                .adminName(user != null ? user.getFullName() : null)
                .adminEmail(user != null ? user.getEmail() : null)
                .createdAt(property.getCreatedAt())
                .updatedAt(property.getUpdatedAt())
                .totalRooms(totalRooms)
                .occupiedRooms(occupiedRooms)
                .availableRooms(availableRooms)
                .totalOccupancy(totalOccupancy)
                .occupancyRate(occupancyRate)
                .build();
    }

    public List<PropertyResponse> toResponseList(List<Property> properties) {
        if (properties == null) return null;
        return properties.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
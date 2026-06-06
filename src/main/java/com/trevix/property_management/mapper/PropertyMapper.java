package com.trevix.property_management.mapper;

import org.springframework.stereotype.Component;
import com.trevix.property_management.dto.request.PropertyCreateRequest;
import com.trevix.property_management.dto.request.PropertyUpdateRequest;
import com.trevix.property_management.dto.response.PropertyResponse;
import com.trevix.property_management.entity.Property;
import com.trevix.property_management.enums.PropertyStatus;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PropertyMapper {
    
    public Property toEntity(PropertyCreateRequest request) {
        if (request == null) {
            return null;
        }
        
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
        if (request == null || property == null) {
            return;
        }
        
        if (request.getName() != null) {
            property.setName(request.getName());
        }
        if (request.getAddress() != null) {
            property.setAddress(request.getAddress());
        }
        if (request.getTimezone() != null) {
            property.setTimezone(request.getTimezone());
        }
        if (request.getCurfewTime() != null && !request.getCurfewTime().isEmpty()) {
            property.setCurfewTime(LocalTime.parse(request.getCurfewTime()));
        }
        if (request.getRoomRules() != null) {
            property.setRoomRules(request.getRoomRules());
        }
        if (request.getStatus() != null) {
            property.setStatus(request.getStatus());
        }
    }
    
    public PropertyResponse toResponse(Property property) {
        if (property == null) {
            return null;
        }
        
        PropertyResponse response = new PropertyResponse();
        response.setPropertyId(property.getId());
        response.setName(property.getName());
        response.setAddress(property.getAddress());
        response.setTimezone(property.getTimezone());
        response.setCurfewTime(property.getCurfewTime() != null ? property.getCurfewTime().toString() : null);
        response.setRoomRules(property.getRoomRules());
        response.setStatus(property.getStatus());
        response.setCreatedAt(property.getCreatedAt());
        response.setUpdatedAt(property.getUpdatedAt());
        
        if (property.getAdmin() != null) {
            response.setAdminId(property.getAdmin().getUserId());
            if (property.getAdmin().getUser() != null) {
                response.setAdminName(property.getAdmin().getUser().getFullName());
            }
        }
        
        return response;
    }
    
    public PropertyDetailResponse toDetailResponse(Property property) {
        if (property == null) {
            return null;
        }
        
        PropertyDetailResponse response = new PropertyDetailResponse();
        response.setPropertyId(property.getId());
        response.setName(property.getName());
        response.setAddress(property.getAddress());
        response.setTimezone(property.getTimezone());
        response.setCurfewTime(property.getCurfewTime() != null ? property.getCurfewTime().toString() : null);
        response.setRoomRules(property.getRoomRules());
        response.setStatus(property.getStatus());
        response.setCreatedAt(property.getCreatedAt());
        response.setUpdatedAt(property.getUpdatedAt());
        
        if (property.getAdmin() != null) {
            response.setAdminId(property.getAdmin().getUserId());
            if (property.getAdmin().getUser() != null) {
                response.setAdminName(property.getAdmin().getUser().getFullName());
                response.setAdminEmail(property.getAdmin().getUser().getEmail());
            }
        }
        
        // Calculate room statistics
        if (property.getRooms() != null) {
            int totalRooms = property.getRooms().size();
            long occupiedRooms = property.getRooms().stream()
                .filter(room -> room.getCurrentOccupancy() != null && room.getCurrentOccupancy() > 0)
                .count();
            long availableRooms = property.getRooms().stream()
                .filter(room -> room.getStatus().toString().equals("AVAILABLE"))
                .count();
            int totalOccupancy = property.getRooms().stream()
                .mapToInt(room -> room.getCurrentOccupancy() != null ? room.getCurrentOccupancy() : 0)
                .sum();
            
            response.setTotalRooms(totalRooms);
            response.setOccupiedRooms(occupiedRooms);
            response.setAvailableRooms(availableRooms);
            response.setTotalOccupancy(totalOccupancy);
            
            if (totalRooms > 0) {
                double occupancyRate = ((double) occupiedRooms / totalRooms) * 100;
                response.setOccupancyRate(Math.round(occupancyRate * 10.0) / 10.0);
            } else {
                response.setOccupancyRate(0.0);
            }
        } else {
            response.setTotalRooms(0);
            response.setOccupiedRooms(0);
            response.setAvailableRooms(0);
            response.setTotalOccupancy(0);
            response.setOccupancyRate(0.0);
        }
        
        return response;
    }
    
    public List<PropertyResponse> toResponseList(List<Property> properties) {
        if (properties == null) {
            return null;
        }
        return properties.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
}
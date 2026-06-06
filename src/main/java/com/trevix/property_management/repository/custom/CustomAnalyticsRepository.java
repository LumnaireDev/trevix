package com.trevix.property_management.repository.custom;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CustomAnalyticsRepository {
    
    Map<String, Object> getOccupancyAnalytics(UUID propertyId, LocalDate startDate, LocalDate endDate);
    
    Map<String, Object> getRevenueAnalytics(UUID propertyId, int year);
    
    List<Map<String, Object>> getMaintenanceAnalytics(UUID propertyId, int year);
    
    Map<String, Object> getSystemWideAnalytics();
}
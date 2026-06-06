package com.trevix.property_management.repository;

import com.trevix.property_management.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {
    
    List<Rating> findByPropertyId(UUID propertyId);
    
    List<Rating> findByTenant_UserId(UUID tenantId);
    
    List<Rating> findByCategory(String category);
    
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.property.id = :propertyId")
    BigDecimal getAverageRatingByProperty(@Param("propertyId") UUID propertyId);
    
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.property.id = :propertyId AND r.category = :category")
    BigDecimal getAverageRatingByPropertyAndCategory(@Param("propertyId") UUID propertyId, @Param("category") String category);
    
    @Query("SELECT r.category, AVG(r.rating) FROM Rating r WHERE r.property.id = :propertyId GROUP BY r.category")
    List<Object[]> getAverageRatingByCategory(@Param("propertyId") UUID propertyId);
}
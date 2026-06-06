package com.trevix.property_management.repository.impl;

import com.trevix.property_management.entity.Tenant;
import com.trevix.property_management.repository.custom.CustomTenantRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class CustomTenantRepositoryImpl implements CustomTenantRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Page<Tenant> findTenantsWithFilters(UUID propertyId, String searchTerm, Boolean isActive, Pageable pageable) {
        StringBuilder jpql = new StringBuilder("SELECT t FROM Tenant t WHERE t.user.property.id = :propertyId");
        
        if (searchTerm != null && !searchTerm.isEmpty()) {
            jpql.append(" AND (t.user.fullName LIKE :searchTerm OR t.user.email LIKE :searchTerm)");
        }
        
        if (isActive != null) {
            jpql.append(" AND t.isActive = :isActive");
        }
        
        Query query = entityManager.createQuery(jpql.toString());
        query.setParameter("propertyId", propertyId);
        
        if (searchTerm != null && !searchTerm.isEmpty()) {
            query.setParameter("searchTerm", "%" + searchTerm + "%");
        }
        
        if (isActive != null) {
            query.setParameter("isActive", isActive);
        }
        
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        
        List<Tenant> tenants = query.getResultList();
        
        // Count query
        StringBuilder countJpql = new StringBuilder("SELECT COUNT(t) FROM Tenant t WHERE t.user.property.id = :propertyId");
        if (searchTerm != null && !searchTerm.isEmpty()) {
            countJpql.append(" AND (t.user.fullName LIKE :searchTerm OR t.user.email LIKE :searchTerm)");
        }
        if (isActive != null) {
            countJpql.append(" AND t.isActive = :isActive");
        }
        
        Query countQuery = entityManager.createQuery(countJpql.toString());
        countQuery.setParameter("propertyId", propertyId);
        if (searchTerm != null && !searchTerm.isEmpty()) {
            countQuery.setParameter("searchTerm", "%" + searchTerm + "%");
        }
        if (isActive != null) {
            countQuery.setParameter("isActive", isActive);
        }
        
        long total = (long) countQuery.getSingleResult();
        
        return new PageImpl<>(tenants, pageable, total);
    }
    
    @Override
    public Map<String, Object> getTenantDashboardStats(UUID propertyId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Total active tenants
        Query activeQuery = entityManager.createQuery(
            "SELECT COUNT(t) FROM Tenant t WHERE t.user.property.id = :propertyId AND t.isActive = true");
        activeQuery.setParameter("propertyId", propertyId);
        stats.put("activeTenants", activeQuery.getSingleResult());
        
        // Average scorecard score
        Query avgScoreQuery = entityManager.createQuery(
            "SELECT AVG(t.scorecardScore) FROM Tenant t WHERE t.user.property.id = :propertyId");
        avgScoreQuery.setParameter("propertyId", propertyId);
        stats.put("avgScorecardScore", avgScoreQuery.getSingleResult());
        
        // Tenants with low scorecard
        Query lowScoreQuery = entityManager.createQuery(
            "SELECT COUNT(t) FROM Tenant t WHERE t.user.property.id = :propertyId AND t.scorecardScore < 50");
        lowScoreQuery.setParameter("propertyId", propertyId);
        stats.put("lowScorecardTenants", lowScoreQuery.getSingleResult());
        
        return stats;
    }
    
    @Override
    public void updateScorecardScores(UUID propertyId) {
        // Complex scorecard calculation logic
        String sql = """
            UPDATE tenants t
            SET scorecard_score = (
                SELECT COALESCE(
                    (SELECT AVG(r.rating) FROM ratings r WHERE r.tenant_id = t.user_id) * 0.4 +
                    (SELECT COUNT(*) FROM payments p 
                     JOIN monthly_bills mb ON p.bill_id = mb.id 
                     WHERE mb.tenant_id = t.user_id AND p.status = 'failed') * -0.1 +
                    (SELECT COUNT(*) FROM maintenance_requests mr 
                     WHERE mr.tenant_id = t.user_id AND mr.status IN ('pending', 'in_progress')) * -0.05,
                    0
                ) * 20
            )
            WHERE t.user_id IN (SELECT u.id FROM users u WHERE u.property_id = :propertyId)
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("propertyId", propertyId);
        query.executeUpdate();
    }
}
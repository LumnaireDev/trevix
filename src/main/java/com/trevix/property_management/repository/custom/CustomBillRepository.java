package com.trevix.property_management.repository.custom;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public interface CustomBillRepository {
    
    Map<String, Object> getFinancialSummary(UUID propertyId, int year, int month);
    
    BigDecimal generateMonthlyBills(UUID propertyId, LocalDate billingMonth);
    
    void processAutomaticPayments();
}
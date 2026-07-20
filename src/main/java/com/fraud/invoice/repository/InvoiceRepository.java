package com.fraud.invoice.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fraud.invoice.enums.InvoiceStatus;
import com.fraud.invoice.enums.RiskLevel;
import com.fraud.invoice.model.Invoice;
import com.fraud.invoice.model.Vendor;

public interface InvoiceRepository extends JpaRepository<Invoice,Long>
{

    // Rule 1 — duplicate check
    List<Invoice> findByVendorAndAmountAndSubmittedAtAfter(
        Vendor vendor, BigDecimal amount, LocalDateTime after);
    
   
 // Rule 2 — split invoice pattern
    @Query("SELECT i FROM Invoice i WHERE i.vendor.id = :vendorId " +
           "AND i.amount BETWEEN :lower AND :upper " +
           "AND i.submittedAt > :after")
    List<Invoice> findRecentInvoicesNearThreshold(
        @Param("vendorId") Long vendorId,
        @Param("lower") BigDecimal lower,
        @Param("upper") BigDecimal upper,
        @Param("after") LocalDateTime after);
    
    
 // Finance dashboard
    List<Invoice> findByStatus(InvoiceStatus status);
    

    // Admin — by risk level, highest score first
    List<Invoice> findByRiskLevelOrderByFraudScoreDesc(RiskLevel level);
    		
    		
    		
    		
}

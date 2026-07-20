package com.fraud.invoice.service;

import com.fraud.invoice.enums.*;
import com.fraud.invoice.model.*;
import com.fraud.invoice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Service
public class FraudScoringEngine {

    @Autowired private InvoiceRepository invoiceRepo;
    @Autowired private FraudLogRepository fraudLogRepo;

    private static final BigDecimal HIGH_VALUE  = new BigDecimal("50000");
    private static final BigDecimal THRESHOLD   = new BigDecimal("10000");
    private static final BigDecimal GST_LIMIT   = new BigDecimal("20000");
    private static final BigDecimal MIN_ROUND   = new BigDecimal("5000");

    public Invoice scoreInvoice(Invoice invoice)
    {
        int score = 0;
        List<FraudLog> logs = new ArrayList<>();

        score += checkDuplicate(invoice, logs);
        score += checkSplitPattern(invoice, logs);
        score += checkNewVendorHighValue(invoice, logs);
        score += checkRoundAmount(invoice, logs);
        score += checkOffHours(invoice, logs);
        score += checkMissingGST(invoice, logs);

        score = Math.min(score, 100);
        invoice.setFraudScore(score);
        invoice.setRiskLevel(getRiskLevel(score));
        if (score >= 60) invoice.setStatus(InvoiceStatus.FLAGGED);
        System.out.println("Final Fraud Score = " + score);
        
        logs.forEach(log -> log.setInvoice(invoice));
        invoice.setFraudLogs(logs);

        return invoice;
    }

    private int checkDuplicate(Invoice inv, List<FraudLog> logs) {
        LocalDateTime ago = LocalDateTime.now().minusDays(30);
        List<Invoice> dups = invoiceRepo
            .findByVendorAndAmountAndSubmittedAtAfter(inv.getVendor(), inv.getAmount(), ago);
        if (!dups.isEmpty()) {
            logs.add(log("DUPLICATE_INVOICE", 35,
                "Same vendor+amount found " + dups.size() + " time(s) in last 30 days"));
            return 35;
        }
        return 0;
    }

    private int checkSplitPattern(Invoice inv, List<FraudLog> logs) {
        BigDecimal lower = THRESHOLD.multiply(new BigDecimal("0.80"));
        LocalDateTime ago = LocalDateTime.now().minusDays(7);
        List<Invoice> splits = invoiceRepo.findRecentInvoicesNearThreshold(
            inv.getVendor().getId(), lower, THRESHOLD, ago);
        if (splits.size() >= 3) {
            logs.add(log("SPLIT_INVOICE_PATTERN", 25,
                splits.size() + " invoices near ₹10,000 threshold in 7 days"));
            return 25;
        }
        return 0;
    }

    private int checkNewVendorHighValue(Invoice inv, List<FraudLog> logs) 
    {
        boolean newVendor = inv.getVendor().getRegisteredAt()
            .isAfter(LocalDate.now().minusDays(30));
        boolean highVal = inv.getAmount().compareTo(HIGH_VALUE) > 0;
        
        System.out.println("newVendor = " + newVendor);
        System.out.println("highVal   = " + highVal);
        
        if (newVendor && highVal) {
            logs.add(log("NEW_VENDOR_HIGH_VALUE", 20,
                "Vendor < 30 days old, invoice ₹" + inv.getAmount()));
            return 20;
        }
        return 0;
    }

    private int checkRoundAmount(Invoice inv, List<FraudLog> logs) {
        boolean round = inv.getAmount().remainder(new BigDecimal("1000")).compareTo(BigDecimal.ZERO) == 0;
        boolean large = inv.getAmount().compareTo(MIN_ROUND) >= 0;
        
        System.out.println("round = " + round);
        System.out.println("large = " + large);
        
        if (round && large) {
            logs.add(log("ROUND_NUMBER_AMOUNT", 15,
                "Amount ₹" + inv.getAmount() + " is suspiciously round"));
            return 15;
        }
        return 0;
    }

    private int checkOffHours(Invoice inv, List<FraudLog> logs) {
        LocalDateTime t = inv.getSubmittedAt();
        if (t == null) t =LocalDateTime.now();
        DayOfWeek day = t.getDayOfWeek();
        int hr = t.getHour();
        boolean weekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
        boolean night   = hr >= 23 || hr < 5;
        if (weekend || night) {
            logs.add(log("OFF_HOURS_SUBMISSION", 10,
                "Submitted at " + t.toLocalTime() + " on " + day));
            return 10;
        }
        return 0;
    }

    private int checkMissingGST(Invoice inv, List<FraudLog> logs)
    {
        boolean aboveLimit = inv.getAmount().compareTo(GST_LIMIT) > 0;
        boolean noGst = inv.getVendor().getGstin() == null || inv.getVendor().getGstin().isBlank();
        
        System.out.println("aboveLimit = " + aboveLimit);
        System.out.println("noGst      = " + noGst);
        
        if (aboveLimit && noGst) {
            logs.add(log("MISSING_GST", 10,
                "Invoice ₹" + inv.getAmount() + " > ₹20,000 but no GSTIN on vendor"));
            return 10;
        }
        return 0;
    }

    private RiskLevel getRiskLevel(int score) {
        if (score >= 60) return RiskLevel.HIGH;
        if (score >= 30) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }

    private FraudLog log(String rule, int pts, String reason) {
        FraudLog l = new FraudLog();
        l.setRuleName(rule); 
        l.setScoreAdded(pts);
        l.setReason(reason);
        return l;
    }
}
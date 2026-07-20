package com.fraud.invoice.model;

import com.fraud.invoice.enums.*;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "invoices")
@Data
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by")
    private User submittedBy;

    @Column(unique = true)
    private String invoiceNumber;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    private String description;
    private LocalDateTime submittedAt;
    private Integer fraudScore;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FraudLog> fraudLogs;

    @PrePersist
    public void prePersist() 
    {
        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }

        if (status == null) {
            status = InvoiceStatus.PENDING;
        }

        if (fraudScore == null) {
            fraudScore = 0;
        }

        if (riskLevel == null) {
            riskLevel = RiskLevel.LOW;
        }
    }
}
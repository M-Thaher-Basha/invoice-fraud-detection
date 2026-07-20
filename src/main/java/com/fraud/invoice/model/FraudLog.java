package com.fraud.invoice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "fraud_logs")
@Data
public class FraudLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;
    
    private String ruleName;
    private Integer scoreAdded;

    @Column(columnDefinition = "TEXT")
    private String reason;

    private LocalDateTime flaggedAt;

    @PrePersist
    public void prePersist() {
        this.flaggedAt = LocalDateTime.now();
    }
}
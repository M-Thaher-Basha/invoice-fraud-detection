package com.fraud.invoice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "vendors")
@Data
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String gstin;
    private String pan;
    private String email;
    private LocalDate registeredAt;
    private Boolean isVerified;

    @JsonIgnore
    @OneToMany(mappedBy = "vendor", fetch = FetchType.LAZY)
    private List<Invoice> invoices;

    @PrePersist
    public void prePersist() {
        this.registeredAt = LocalDate.now();
        this.isVerified = false;
    }
}
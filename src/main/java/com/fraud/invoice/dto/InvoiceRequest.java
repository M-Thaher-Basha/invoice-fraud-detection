package com.fraud.invoice.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class InvoiceRequest {
    private Long vendorId;
    private String invoiceNumber;
    private BigDecimal amount;
    private String description;
    
}
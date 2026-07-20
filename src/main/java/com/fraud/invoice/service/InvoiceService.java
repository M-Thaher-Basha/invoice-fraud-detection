package com.fraud.invoice.service;

import com.fraud.invoice.enums.InvoiceStatus;
import com.fraud.invoice.model.Invoice;
import com.fraud.invoice.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired private InvoiceRepository invoiceRepo;
    @Autowired private FraudScoringEngine scoringEngine;

    @Transactional
    public Invoice submitInvoice(Invoice invoice) {
        invoice = scoringEngine.scoreInvoice(invoice);
        return invoiceRepo.save(invoice);
    }

    public List<Invoice> getFlaggedInvoices() {
        return invoiceRepo.findByStatus(InvoiceStatus.FLAGGED);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepo.findAll();
    }

    @Transactional
    public Invoice approve(Long id) {
        Invoice inv = invoiceRepo.findById(id).orElseThrow(() -> new RuntimeException("Invoice not found"));
        inv.setStatus(InvoiceStatus.APPROVED);
        return invoiceRepo.save(inv);
    }

    @Transactional
    public Invoice reject(Long id) {
        Invoice inv = invoiceRepo.findById(id).orElseThrow(() -> new RuntimeException("Invoice not found"));
        inv.setStatus(InvoiceStatus.REJECTED);
        return invoiceRepo.save(inv);
    }
}
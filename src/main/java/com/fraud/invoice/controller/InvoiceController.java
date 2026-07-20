package com.fraud.invoice.controller;

import com.fraud.invoice.dto.InvoiceRequest;
import com.fraud.invoice.model.*;
import com.fraud.invoice.repository.*;
import com.fraud.invoice.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired private InvoiceService invoiceService;
    @Autowired private VendorRepository vendorRepo;
    @Autowired private FraudLogRepository fraudLogRepo;
    @Autowired private UserRepository userRepo;

    // VENDOR submits an invoice
    @PostMapping
    public ResponseEntity<Invoice> submit(@RequestBody InvoiceRequest req, Authentication authentication) {
        Vendor vendor = vendorRepo.findById(req.getVendorId()).orElseThrow(() -> new RuntimeException("Vendor not found"));
        
        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Invoice inv = new Invoice();
        inv.setVendor(vendor);
        inv.setSubmittedBy(user); 
        inv.setInvoiceNumber(req.getInvoiceNumber());
        inv.setAmount(req.getAmount());
        inv.setDescription(req.getDescription());
        return ResponseEntity.ok(invoiceService.submitInvoice(inv));
    }

    // FINANCE views flagged invoices
    @GetMapping("/flagged")
    public List<Invoice> getFlagged() {
        return invoiceService.getFlaggedInvoices();
    }

    // View all invoices
    @GetMapping
    public List<Invoice> getAll() {
        return invoiceService.getAllInvoices();
    }

    // View fraud logs for a specific invoice
    @GetMapping("/{id}/fraud-logs")
    public List<FraudLog> getLogs(@PathVariable Long id) {
        return fraudLogRepo.findByInvoiceId(id);
    }

    // FINANCE approves
    @PutMapping("/{id}/approve")
    public ResponseEntity<Invoice> approve(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.approve(id));
    }

    // FINANCE rejects
    @PutMapping("/{id}/reject")
    public ResponseEntity<Invoice> reject(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.reject(id));
    }
}
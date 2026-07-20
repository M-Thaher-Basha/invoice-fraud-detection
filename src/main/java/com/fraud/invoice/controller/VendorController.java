package com.fraud.invoice.controller;

import com.fraud.invoice.model.Vendor;
import com.fraud.invoice.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    @Autowired private VendorRepository vendorRepo;

    @PostMapping
    public ResponseEntity<Vendor> create(@RequestBody Vendor vendor) {
        return ResponseEntity.ok(vendorRepo.save(vendor));
    }

    @GetMapping
    public List<Vendor> getAll() {
        return vendorRepo.findAll();
    }
}
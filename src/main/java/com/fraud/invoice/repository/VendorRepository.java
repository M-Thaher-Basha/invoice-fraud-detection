package com.fraud.invoice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fraud.invoice.model.Vendor;

public interface VendorRepository extends JpaRepository<Vendor,Long>
{

}

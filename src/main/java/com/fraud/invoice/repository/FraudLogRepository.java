package com.fraud.invoice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fraud.invoice.model.FraudLog;

public interface FraudLogRepository extends JpaRepository<FraudLog,Long>
{
	List<FraudLog> findByInvoiceId(long invoiceId);
	
	List<FraudLog> findByRuleName(String RuleName);
}

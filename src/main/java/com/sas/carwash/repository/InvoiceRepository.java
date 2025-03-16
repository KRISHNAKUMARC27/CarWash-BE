package com.sas.carwash.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.Invoice;

public interface InvoiceRepository extends MongoRepository<Invoice, String> {
	
	List<Invoice> findAllByOrderByIdDesc();
	Invoice findByJobObjId(String jobObjId);
	List<Invoice> findByCreditFlagAndCreditSettledFlagOrderByIdDesc(Boolean creditFlag, Boolean creditSettledFlag);
	List<Invoice> findByBillCloseDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
}

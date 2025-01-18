package com.sas.carwash.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.Invoice;

public interface InvoiceRepository extends MongoRepository<Invoice, String> {
	
	List<Invoice> findAllByOrderByIdDesc();
	Invoice findByJobObjId(String jobObjId);

}

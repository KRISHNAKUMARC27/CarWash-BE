package com.sas.carwash.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.ReceiptInvoice;

public interface ReceiptInvoiceRepository extends MongoRepository<ReceiptInvoice, String> {
	
	List<ReceiptInvoice> findAllByOrderByIdDesc();

}

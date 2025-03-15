package com.sas.carwash.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.ReceiptEstimate;

public interface ReceiptEstimateRepository extends MongoRepository<ReceiptEstimate, String> {
	
	List<ReceiptEstimate> findAllByOrderByIdDesc();

}

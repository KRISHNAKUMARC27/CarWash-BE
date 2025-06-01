package com.sas.carwash.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.Payments;

public interface PaymentsRepository extends MongoRepository<Payments, String> {

	List<Payments> findAllByOrderByIdDesc();

	List<Payments> findByPaymentDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
	List<Payments> findByPaymentDate(LocalDateTime paymentDate);
	
}

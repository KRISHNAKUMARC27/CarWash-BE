package com.sas.carwash.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.Expense;

public interface ExpenseRepository extends MongoRepository<Expense, String> {

	List<Expense> findAllByOrderByIdDesc();

	List<Expense> findByDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
	
}

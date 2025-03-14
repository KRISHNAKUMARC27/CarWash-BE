package com.sas.carwash.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.Expense;

public interface ExpenseRepository extends MongoRepository<Expense, String> {
	
}

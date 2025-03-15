package com.sas.carwash.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.Appointment;
import com.sas.carwash.entity.Expense;

public interface ExpenseRepository extends MongoRepository<Expense, String> {

	List<Appointment> findAllByOrderByIdDesc();
	
}

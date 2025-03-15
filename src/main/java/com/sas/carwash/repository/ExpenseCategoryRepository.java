package com.sas.carwash.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.ExpenseCategory;

public interface ExpenseCategoryRepository extends MongoRepository<ExpenseCategory, String> {

	ExpenseCategory findByCategory(String category);

}

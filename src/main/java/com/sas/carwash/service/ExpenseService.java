package com.sas.carwash.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.sas.carwash.entity.Expense;
import com.sas.carwash.repository.ExpenseRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ExpenseService {
	
	private final ExpenseRepository expenseRepository;
	
	public Expense saveExpense(Expense expense) {
		expense.setExpenseDate(LocalDateTime.now());
		return expenseRepository.save(expense);
	}

}

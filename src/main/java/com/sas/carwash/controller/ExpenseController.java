package com.sas.carwash.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sas.carwash.entity.Appointment;
import com.sas.carwash.entity.Expense;
import com.sas.carwash.service.ExpenseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/expense")
@RequiredArgsConstructor
public class ExpenseController {

	private final ExpenseService expenseService;

	@PostMapping
	public Expense createExpense(@RequestBody Expense expense) {
		return expenseService.save(expense);
	}
	
	@PostMapping("/salaryAdvance")
	public Expense saveSalaryAdvance(@RequestBody Expense expense) {
		return expenseService.saveSalaryAdvance(expense);
	}
	
	@GetMapping
	public List<Appointment> getAllExpenses() {
		return expenseService.getAllExpenses();
	}

}

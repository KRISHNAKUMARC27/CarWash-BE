package com.sas.carwash.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sas.carwash.entity.Appointment;
import com.sas.carwash.entity.Expense;
import com.sas.carwash.entity.ExpenseCategory;
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
	public List<?> getAllExpenses() {
		return expenseService.getAllExpenses();
	}

	@GetMapping("/expenseCategory")
	public List<?> findAllExpenseCategory() {
		return expenseService.findAllExpenseCategory();
	}

	@PostMapping("/saveExpenseCategory")
	public ResponseEntity<?> saveExpenseCategory(@RequestBody ExpenseCategory expenseCategory) {
		try {
			return ResponseEntity.ok().body(expenseService.saveExpenseCategory(expenseCategory));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@DeleteMapping("/expenseCategory/{id}")
	public ResponseEntity<?> deleteExpenseCategory(@PathVariable String id) {
		try {
			return ResponseEntity.ok().body(expenseService.deleteExpenseCategoryById(id));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/expenseCategory/{oldCategory}/{newCategory}")
	public ResponseEntity<?> updateExpenseCategory(@PathVariable String oldCategory, @PathVariable String newCategory) {
		try {
			return ResponseEntity.ok().body(expenseService.updateExpenseCategory(oldCategory, newCategory));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	//REPORTING
	@GetMapping("/report/daily/{date}")
	public Map<String, Object> getDailyExpense(@PathVariable String date) {
		return expenseService.getDailyExpense(LocalDate.parse(date));
	}

	@GetMapping("/report/weekly/{year}/{week}")
	public Map<String, Object> getWeeklyExpense(@PathVariable int year, @PathVariable int week) {
		return expenseService.getWeeklyExpense(year, week);
	}

	@GetMapping("/report/monthly/{year}/{month}")
	public Map<String, Object> getMonthlyExpense(@PathVariable int year, @PathVariable int month) {
		return expenseService.getMonthlyExpense(year, month);
	}

	@GetMapping("/report/yearly/{year}")
	public Map<String, Object> getYearlyExpense(@PathVariable int year) {
		return expenseService.getYearlyExpense(year);
	}

	@GetMapping("/report/daterange")
	public Map<String, Object> getExpenseByDateRange(@RequestParam String startDate, @RequestParam String endDate) {
		return expenseService.getExpenseByDateRange(LocalDate.parse(startDate), LocalDate.parse(endDate));
	}

}

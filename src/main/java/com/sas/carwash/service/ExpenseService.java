package com.sas.carwash.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.sas.carwash.entity.Employee;
import com.sas.carwash.entity.EmployeeSalary;
import com.sas.carwash.entity.Expense;
import com.sas.carwash.entity.ExpenseCategory;
import com.sas.carwash.repository.EmployeeRepository;
import com.sas.carwash.repository.ExpenseCategoryRepository;
import com.sas.carwash.repository.ExpenseRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ExpenseService {

	private final ExpenseRepository expenseRepository;
	private final EmployeeRepository employeeRepository;
	private final ExpenseCategoryRepository expenseCategoryRepository;
	private final MongoTemplate mongoTemplate;

	public Expense saveSalaryExpense(EmployeeSalary empSalary) {
		Expense expense = Expense.builder().type("SALARY").expenseAmount(empSalary.getSalaryPaid())
				.desc(empSalary.getName() + " - SALARY").paymentMode(empSalary.getPaymentMode())
				.date(LocalDateTime.now()).empId(empSalary.getEmpId()).build();
		return expenseRepository.save(expense);
	}

	public Expense save(Expense expense) {
		expense.setDate(LocalDateTime.now());
		return expenseRepository.save(expense);
	}

	public Expense saveSalaryAdvance(Expense expense) {

		Employee employee = employeeRepository.findById(expense.getEmpId())
				.orElseThrow(() -> new RuntimeException("Employee not found"));
		BigDecimal currentAdvance = employee.getSalaryAdvance() != null ? employee.getSalaryAdvance() : BigDecimal.ZERO;
		employee.setSalaryAdvance(currentAdvance.add(expense.getExpenseAmount()));
		employeeRepository.save(employee);

		expense.setDate(LocalDateTime.now());
		return expenseRepository.save(expense);
	}

	public List<Expense> getAllExpenses() {
		return expenseRepository.findAllByOrderByIdDesc();
	}

	public ExpenseCategory saveExpenseCategory(ExpenseCategory expenseCategory) throws Exception {
		ExpenseCategory category = expenseCategoryRepository.findByCategory(expenseCategory.getCategory());
		if (category == null)
			return expenseCategoryRepository.save(expenseCategory);
		else {
			throw new Exception(expenseCategory.getCategory() + " is already available as ExpenseCategory");
		}
	}

	public synchronized ExpenseCategory deleteExpenseCategoryById(String id) throws Exception {
		ExpenseCategory expenseCategory = expenseCategoryRepository.findById(id).orElse(null);

		expenseCategoryRepository.deleteById(id);
		return expenseCategory;
	}

	private void updateType(String oldCategory, String newCategory) {
		Query query = new Query(Criteria.where("type").is(oldCategory));
		Update update = new Update().set("type", newCategory);
		mongoTemplate.updateMulti(query, update, Expense.class);
	}

	public ExpenseCategory updateExpenseCategory(String oldCategory, String newCategory) {
		ExpenseCategory expenseCategory = expenseCategoryRepository.findByCategory(oldCategory);
		if (expenseCategory != null) {
			expenseCategory.setCategory(newCategory);
			expenseCategory = expenseCategoryRepository.save(expenseCategory);
			updateType(oldCategory, newCategory);
		}
		return expenseCategory;
	}

	public List<?> findAllExpenseCategory() {
		return expenseCategoryRepository.findAll();
	}

	// REPORT START
	private Map<String, Object> processExpense(List<Expense> records) {
		Map<String, Object> result = new HashMap<>();

		// Total expense amount
		BigDecimal total = records.stream()
				.map(exp -> Optional.ofNullable(exp.getExpenseAmount()).orElse(BigDecimal.ZERO))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		result.put("total", total);

		// Group by type
		Map<String, BigDecimal> byType = records.stream()
				.collect(Collectors.groupingBy(exp -> Optional.ofNullable(exp.getType()).orElse("Unknown"),
						Collectors.reducing(BigDecimal.ZERO,
								exp -> Optional.ofNullable(exp.getExpenseAmount()).orElse(BigDecimal.ZERO),
								BigDecimal::add)));
		result.put("byType", byType);

		// Group by paymentMode
		Map<String, BigDecimal> byPaymentMode = records.stream()
				.collect(Collectors.groupingBy(exp -> Optional.ofNullable(exp.getPaymentMode()).orElse("Unknown"),
						Collectors.reducing(BigDecimal.ZERO,
								exp -> Optional.ofNullable(exp.getExpenseAmount()).orElse(BigDecimal.ZERO),
								BigDecimal::add)));
		result.put("byPaymentMode", byPaymentMode);

		return result;
	}

	public Map<String, Object> getDailyExpense(LocalDate date) {
		LocalDateTime start = date.atStartOfDay();
		LocalDateTime end = start.plusDays(1);
		List<Expense> records = expenseRepository.findByDateBetween(start, end);
		return processExpense(records);
	}

	public Map<String, Object> getWeeklyExpense(int year, int week) {
		List<Expense> records = expenseRepository.findAll().stream().filter(e -> {
			LocalDateTime dateTime = e.getDate();
			return dateTime.getYear() == year && dateTime.get(WeekFields.ISO.weekOfYear()) == week;
		}).collect(Collectors.toList());
		return processExpense(records);
	}

	public Map<String, Object> getMonthlyExpense(int year, int month) {
		List<Expense> records = expenseRepository.findAll().stream()
				.filter(e -> e.getDate().getYear() == year && e.getDate().getMonthValue() == month)
				.collect(Collectors.toList());
		return processExpense(records);
	}

	public Map<String, Object> getYearlyExpense(int year) {
		List<Expense> records = expenseRepository.findAll().stream().filter(e -> e.getDate().getYear() == year)
				.collect(Collectors.toList());
		return processExpense(records);
	}

	public Map<String, Object> getExpenseByDateRange(LocalDate start, LocalDate end) {
		LocalDateTime startDateTime = start.atStartOfDay();
		LocalDateTime endDateTime = end.plusDays(1).atStartOfDay();
		List<Expense> records = expenseRepository.findByDateBetween(startDateTime, endDateTime);
		return processExpense(records);
	}

	// REPORT END
}

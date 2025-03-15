package com.sas.carwash.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sas.carwash.entity.Appointment;
import com.sas.carwash.entity.Employee;
import com.sas.carwash.entity.EmployeeSalary;
import com.sas.carwash.entity.Expense;
import com.sas.carwash.repository.EmployeeRepository;
import com.sas.carwash.repository.ExpenseRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ExpenseService {

	private final ExpenseRepository expenseRepository;
	private final EmployeeRepository employeeRepository;

	public Expense saveSalaryExpense(EmployeeSalary empSalary) {
		Expense expense = Expense.builder().type("SALARY").expenseAmount(empSalary.getSalaryPaid())
				.desc(empSalary.getName() + " - SALARY").paymentMode(empSalary.getPaymentMode())
				.expenseDate(LocalDateTime.now()).empId(empSalary.getEmpId()).build();
		return expenseRepository.save(expense);
	}

	public Expense save(Expense expense) {
		expense.setExpenseDate(LocalDateTime.now());
		return expenseRepository.save(expense);
	}

	public Expense saveSalaryAdvance(Expense expense) {

		Employee employee = employeeRepository.findById(expense.getEmpId())
				.orElseThrow(() -> new RuntimeException("Employee not found"));
		BigDecimal currentAdvance = employee.getSalaryAdvance() != null ? employee.getSalaryAdvance() : BigDecimal.ZERO;
		employee.setSalaryAdvance(currentAdvance.add(expense.getExpenseAmount()));
		employeeRepository.save(employee);
		
		expense.setExpenseDate(LocalDateTime.now());
		return expenseRepository.save(expense);
	}

	public List<Appointment> getAllExpenses() {
		return expenseRepository.findAllByOrderByIdDesc();
	}

}

package com.sas.carwash.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sas.carwash.entity.Department;
import com.sas.carwash.entity.Employee;
import com.sas.carwash.service.EmployeeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

	private final EmployeeService employeeService;

	@GetMapping
	public List<?> findAll() {
		return employeeService.findAllEmployee();
	}

	@PostMapping
	public Employee save(@RequestBody Employee employee) {
		return employeeService.saveEmployee(employee);
	}

	@GetMapping("/department")
	public List<?> findAllDepartment() {
		return employeeService.findAllDepartment();
	}

	@PostMapping("/department")
	public ResponseEntity<?> saveDepartment(@RequestBody Department department) {
		try {
			return ResponseEntity.ok().body(employeeService.saveDepartment(department));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@DeleteMapping("/department/{id}")
	public ResponseEntity<?> deleteDepartmentById(@PathVariable String id) {
		try {
			return ResponseEntity.ok().body(employeeService.deleteDepartmentById(id));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/department/{oldDept}/{newDept}")
	public ResponseEntity<?> updateDepartment(@PathVariable String oldDept, @PathVariable String newDept) {
		try {
			return ResponseEntity.ok().body(employeeService.updateDepartment(oldDept, newDept));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}

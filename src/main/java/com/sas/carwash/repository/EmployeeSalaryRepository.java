package com.sas.carwash.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.EmployeeSalary;

public interface EmployeeSalaryRepository extends MongoRepository<EmployeeSalary, String> {

	boolean existsByEmpIdAndSalaryDateBetween(String empId, LocalDateTime startDate, LocalDateTime endDate);
   
}

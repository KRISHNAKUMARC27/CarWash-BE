package com.sas.carwash.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.Employee;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
    Integer countByDepartment(String department);
}

package com.sas.carwash.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.Department;

public interface DepartmentRepository extends MongoRepository<Department, String> {
	
	Department findByDepartmentName(String departmentName);
    
}

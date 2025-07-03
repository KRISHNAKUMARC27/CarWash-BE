package com.sas.carwash.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.LabourCategory;

public interface LabourCategoryRepository extends MongoRepository<LabourCategory, String> {

	LabourCategory findByCategory(String category);

}

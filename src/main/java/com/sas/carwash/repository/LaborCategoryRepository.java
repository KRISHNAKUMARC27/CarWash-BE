package com.sas.carwash.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.LaborCategory;

public interface LaborCategoryRepository extends MongoRepository<LaborCategory, String> {

	LaborCategory findByCategory(String category);

}

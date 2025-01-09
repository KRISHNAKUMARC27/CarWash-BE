package com.sas.carwash.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.ExternalWorkCategory;

public interface ExternalWorkCategoryRepository extends MongoRepository<ExternalWorkCategory, String> {

	ExternalWorkCategory findByCategory(String category);

}

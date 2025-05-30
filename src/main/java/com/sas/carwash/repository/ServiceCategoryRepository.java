package com.sas.carwash.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.ServiceCategory;

public interface ServiceCategoryRepository extends MongoRepository<ServiceCategory, String> {

	ServiceCategory findByCategory(String category);

}

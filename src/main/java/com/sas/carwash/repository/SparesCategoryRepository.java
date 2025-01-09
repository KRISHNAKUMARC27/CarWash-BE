package com.sas.carwash.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.SparesCategory;

public interface SparesCategoryRepository extends MongoRepository<SparesCategory, String> {

	SparesCategory findByCategory(String category);

}

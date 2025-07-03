package com.sas.carwash.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.LabourInventory;

public interface LabourInventoryRepository extends MongoRepository<LabourInventory, String>, LabourInventoryFilter {

	List<LabourInventory> findAllByOrderByIdDesc();
	LabourInventory findByDescAndCategory(String desc, String category);
	//void deleteByCategory(String category);
	Integer countByCategory(String category); 
}

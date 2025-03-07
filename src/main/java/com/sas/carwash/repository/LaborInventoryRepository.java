package com.sas.carwash.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.LaborInventory;

public interface LaborInventoryRepository extends MongoRepository<LaborInventory, String>, LaborInventoryFilter{

	List<LaborInventory> findAllByOrderByIdDesc();
	LaborInventory findByDescAndCategory(String desc, String category);
	//void deleteByCategory(String category);
	Integer countByCategory(String category); 
}

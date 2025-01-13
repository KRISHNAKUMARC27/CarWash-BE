package com.sas.carwash.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.ServiceInventory;

public interface ServiceInventoryRepository extends MongoRepository<ServiceInventory, String>, ServiceInventoryFilter {

	List<ServiceInventory> findAllByOrderByIdDesc();
	ServiceInventory findByDescAndCategory(String desc, String category);
	//void deleteByCategory(String category);
	Integer countByCategory(String category); 
}

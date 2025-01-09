package com.sas.carwash.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.SparesInventory;

public interface SparesInventoryRepository extends MongoRepository<SparesInventory, String>, SparesInventoryFilter {

	List<SparesInventory> findAllByOrderByIdDesc();
	List<SparesInventory> findAllByOrderByUpdateDateDesc();
	SparesInventory findByDescAndCategory(String desc, String category);
	//void deleteByCategory(String category);
	Integer countByCategory(String category); 
}

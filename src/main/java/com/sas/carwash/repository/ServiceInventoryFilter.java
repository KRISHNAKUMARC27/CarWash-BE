package com.sas.carwash.repository;

import java.util.List;

import com.sas.carwash.entity.ServiceInventory;

public interface ServiceInventoryFilter {
	List<ServiceInventory> findServiceInventoryWithFilter(List<String> categoryList, String desc);
	void updateCategory(String oldCategory, String newCategory);
	
}

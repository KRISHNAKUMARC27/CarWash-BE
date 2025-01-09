package com.sas.carwash.repository;

import java.util.List;

import com.sas.carwash.entity.LaborInventory;

public interface LaborInventoryFilter {
	List<LaborInventory> findLaborInventoryWithFilter(List<String> categoryList, String desc);
	void updateCategory(String oldCategory, String newCategory);
	
}

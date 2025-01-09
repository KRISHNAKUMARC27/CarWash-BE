package com.sas.carwash.repository;

import java.util.List;

import com.sas.carwash.entity.SparesInventory;

public interface SparesInventoryFilter {
	List<SparesInventory> findSparesInventoryWithFilter(List<String> categoryList, String desc);
	void updateCategory(String oldCategory, String newCategory);
	
}

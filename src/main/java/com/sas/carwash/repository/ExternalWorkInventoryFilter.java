package com.sas.carwash.repository;

import java.util.List;

import com.sas.carwash.entity.ExternalWorkInventory;

public interface ExternalWorkInventoryFilter {
	List<ExternalWorkInventory> findExternalWorkInventoryWithFilter(List<String> categoryList, String desc);
	void updateCategory(String oldCategory, String newCategory);
	
}

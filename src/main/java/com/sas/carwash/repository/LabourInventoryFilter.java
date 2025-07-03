package com.sas.carwash.repository;

import java.util.List;

import com.sas.carwash.entity.LabourInventory;

public interface LabourInventoryFilter {
	List<LabourInventory> findLabourInventoryWithFilter(List<String> categoryList, String desc);
	void updateCategory(String oldCategory, String newCategory);
	
}

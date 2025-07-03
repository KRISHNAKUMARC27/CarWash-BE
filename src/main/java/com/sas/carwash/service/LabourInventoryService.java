package com.sas.carwash.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sas.carwash.entity.LabourCategory;
import com.sas.carwash.entity.LabourInventory;
import com.sas.carwash.model.LabourFilter;
import com.sas.carwash.repository.LabourCategoryRepository;
import com.sas.carwash.repository.LabourInventoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LabourInventoryService {

	private final LabourInventoryRepository labourInventoryRepository;
	private final LabourCategoryRepository labourCategoryRepository;
	// private final SparesEventsRepository sparesEventsRepository;

	public List<?> findAll() {
		return labourInventoryRepository.findAllByOrderByIdDesc();
	}

	public LabourInventory findById(String id) throws Exception {
		return labourInventoryRepository.findById(id).orElse(null);
	}

	public synchronized LabourInventory save(LabourInventory labour) throws Exception {
		String oldLabourCategory = null;
		if (labour.getId() != null) {
			// the labour is getting updated.
			LabourInventory oldLabour = labourInventoryRepository.findById(labour.getId()).orElse(null);
			if (oldLabour != null && !oldLabour.getCategory().equals(labour.getCategory())) {
				oldLabourCategory = oldLabour.getCategory();
			}
		} else {

			LabourInventory existingLabourInventory = labourInventoryRepository
					.findByDescAndCategory(labour.getDesc(), labour.getCategory());
			if (existingLabourInventory != null) {
				throw new Exception("Labour Name " + labour.getDesc() + " already exists for the category");
			}
		}
		labour = labourInventoryRepository.save(labour);

		Integer sparesCategoryCount = labourInventoryRepository.countByCategory(labour.getCategory());
		LabourCategory sparesCategory = labourCategoryRepository.findByCategory(labour.getCategory());
		sparesCategory.setCount(sparesCategoryCount);
		labourCategoryRepository.save(sparesCategory);

		if (oldLabourCategory != null) {
			Integer oldLabourCategoryCount = labourInventoryRepository.countByCategory(oldLabourCategory);
			LabourCategory oldSparesCat = labourCategoryRepository.findByCategory(oldLabourCategory);
			oldSparesCat.setCount(oldLabourCategoryCount);
			labourCategoryRepository.save(oldSparesCat);
		}

		return labour;
	}

	public List<LabourInventory> findLabourInventoryWithFilter(LabourFilter labourFilter) {
		return labourInventoryRepository.findLabourInventoryWithFilter(labourFilter.categoryList(),
				labourFilter.desc());
	}

	public List<?> findAllLabourCategory() {
		return labourCategoryRepository.findAll();
	}

	public LabourCategory saveLabourCategory(LabourCategory labourCategory) throws Exception {
		LabourCategory category = labourCategoryRepository.findByCategory(labourCategory.getCategory());
		if (category == null)
			return labourCategoryRepository.save(labourCategory);
		else {
			throw new Exception(labourCategory.getCategory() + " is already available as LabourCategory");
		}
	}

	public synchronized LabourCategory deleteLabourCategoryById(String id) throws Exception {
		LabourCategory labourCategory = labourCategoryRepository.findById(id).orElse(null);

		if (labourCategory != null) {
			// sparesInventoryRepository.updateCategory(sparesCategory.getCategory(), "");
			if (labourCategory.getCount() != null && labourCategory.getCount() > 0)
				throw new Exception(
						"Cannot delete category as its has " + labourCategory.getCount() + " Labours reffering to ");
		} else {
			throw new Exception("Invalid id for deleteLabourCategoryById " + id);
		}

		labourCategoryRepository.deleteById(id);
		return labourCategory;
	}

	public LabourCategory updateLabourCategory(String oldCategory, String newCategory) {
		LabourCategory labourCategory = labourCategoryRepository.findByCategory(oldCategory);
		if (labourCategory != null) {
			labourCategory.setCategory(newCategory);
			labourCategory = labourCategoryRepository.save(labourCategory);
			labourInventoryRepository.updateCategory(oldCategory, newCategory);
		}
		return labourCategory;
	}

}

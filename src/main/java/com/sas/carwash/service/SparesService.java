package com.sas.carwash.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sas.carwash.entity.SparesCategory;
import com.sas.carwash.entity.SparesEvents;
import com.sas.carwash.entity.SparesInventory;
import com.sas.carwash.model.SparesFilter;
import com.sas.carwash.repository.SparesCategoryRepository;
import com.sas.carwash.repository.SparesEventsRepository;
import com.sas.carwash.repository.SparesInventoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SparesService {

	private final SparesInventoryRepository sparesInventoryRepository;
	private final SparesCategoryRepository sparesCategoryRepository;
	private final SparesEventsRepository sparesEventsRepository;

	public List<?> findAll() {
		return sparesInventoryRepository.findAllByOrderByUpdateDateDesc();
	}
	
	public SparesInventory findById(String id) throws Exception {
		return sparesInventoryRepository.findById(id).orElse(null);
	}

	public void saveFromJobSpares(SparesInventory spares) {
		LocalDateTime currentTime = LocalDateTime.now();
		spares.setUpdateDate(currentTime);
		if (spares.getMinThresh().compareTo(spares.getQty()) > 0) {
			spares.setMinThreshDate(currentTime);
			// send notification or dashboard alerts
			SparesEvents sparesEvent = SparesEvents.builder().sparesId(spares.getId())
					.notif(spares.getDesc() + " shortage").time(currentTime).build();
			sparesEventsRepository.save(sparesEvent);
		}
		sparesInventoryRepository.save(spares);
	}

	public synchronized SparesInventory save(SparesInventory spares) throws Exception  {
		LocalDateTime currentTime = LocalDateTime.now();
		spares.setUpdateDate(currentTime);
		if (spares.getMinThresh().compareTo(spares.getQty()) > 0) {
			spares.setMinThreshDate(currentTime);
			// send notification or dashboard alerts
			SparesEvents sparesEvent = SparesEvents.builder().sparesId(spares.getId())
					.notif(spares.getDesc() + " shortage").time(currentTime).build();
			sparesEventsRepository.save(sparesEvent);
		}

		String oldSparesCategory = null;
		if (spares.getId() != null) {
			// the spares is getting updated.
			SparesInventory oldSpares = sparesInventoryRepository.findById(spares.getId()).orElse(null);
			if (oldSpares != null && !oldSpares.getCategory().equals(spares.getCategory())) {
				oldSparesCategory = oldSpares.getCategory();
			}
		} else {

			SparesInventory existingServiceInventory = sparesInventoryRepository
					.findByDescAndCategory(spares.getDesc(), spares.getCategory());
			if (existingServiceInventory != null) {
				throw new Exception("Spares Name " + spares.getDesc() + " already exists for the category");
			}
		}

		spares = sparesInventoryRepository.save(spares);

		Integer sparesCategoryCount = sparesInventoryRepository.countByCategory(spares.getCategory());
		SparesCategory sparesCategory = sparesCategoryRepository.findByCategory(spares.getCategory());
		sparesCategory.setSparesCount(sparesCategoryCount);
		sparesCategoryRepository.save(sparesCategory);

		if (oldSparesCategory != null) {
			Integer oldSparesCategoryCount = sparesInventoryRepository.countByCategory(oldSparesCategory);
			SparesCategory oldSparesCat = sparesCategoryRepository.findByCategory(oldSparesCategory);
			oldSparesCat.setSparesCount(oldSparesCategoryCount);
			sparesCategoryRepository.save(oldSparesCat);
		}

		return spares;
	}

	public List<SparesInventory> findSparesInventoryWithFilter(SparesFilter sparesFilter) {
		return sparesInventoryRepository.findSparesInventoryWithFilter(sparesFilter.categoryList(),
				sparesFilter.desc());
	}

	public List<?> findAllSparesCategory() {
		return sparesCategoryRepository.findAll();
	}

	public SparesCategory saveSparesCategory(SparesCategory sparesCategory) throws Exception {
		SparesCategory category = sparesCategoryRepository.findByCategory(sparesCategory.getCategory());
		if (category == null)
			return sparesCategoryRepository.save(sparesCategory);
		else {
			throw new Exception(sparesCategory.getCategory() + " is already available as SparesCategory");
		}
	}

	public synchronized SparesCategory deleteSparesCategoryById(String id) throws Exception {
		SparesCategory sparesCategory = sparesCategoryRepository.findById(id).orElse(null);

		if (sparesCategory != null) {
			// sparesInventoryRepository.updateCategory(sparesCategory.getCategory(), "");
			if (sparesCategory.getSparesCount() != null && sparesCategory.getSparesCount() > 0)
				throw new Exception("Cannot delete category as its has " + sparesCategory.getSparesCount()
						+ " Spares reffering to ");
		} else {
			throw new Exception("Invalid id for deleteSparesCategoryById " + id);
		}

		sparesCategoryRepository.deleteById(id);
		return sparesCategory;
	}

	public SparesCategory updateSparesCategory(String oldCategory, String newCategory) {
		SparesCategory sparesCategory = sparesCategoryRepository.findByCategory(oldCategory);
		if (sparesCategory != null) {
			sparesCategory.setCategory(newCategory);
			sparesCategory = sparesCategoryRepository.save(sparesCategory);
			sparesInventoryRepository.updateCategory(oldCategory, newCategory);
		}
		return sparesCategory;
	}

}

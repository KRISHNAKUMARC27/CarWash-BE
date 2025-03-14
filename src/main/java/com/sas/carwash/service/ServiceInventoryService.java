package com.sas.carwash.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sas.carwash.entity.ServiceCategory;
import com.sas.carwash.entity.ServiceInventory;
import com.sas.carwash.model.ServiceFilter;
import com.sas.carwash.repository.ServiceCategoryRepository;
import com.sas.carwash.repository.ServiceInventoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceInventoryService {

	private final ServiceInventoryRepository serviceInventoryRepository;
	private final ServiceCategoryRepository serviceCategoryRepository;
	// private final SparesEventsRepository sparesEventsRepository;

	public List<?> findAll() {
		return serviceInventoryRepository.findAllByOrderByIdDesc();
	}

	public ServiceInventory findById(String id) throws Exception {
		return serviceInventoryRepository.findById(id).orElse(null);
	}

	public synchronized ServiceInventory save(ServiceInventory service) throws Exception {
		String oldServiceCategory = null;
		if (service.getId() != null) {
			// the service is getting updated.
			ServiceInventory oldService = serviceInventoryRepository.findById(service.getId()).orElse(null);
			if (oldService != null && !oldService.getCategory().equals(service.getCategory())) {
				oldServiceCategory = oldService.getCategory();
			}
		} else {

			ServiceInventory existingServiceInventory = serviceInventoryRepository
					.findByDescAndCategory(service.getDesc(), service.getCategory());
			if (existingServiceInventory != null) {
				throw new Exception("Service Name " + service.getDesc() + " already exists for the category");
			}
		}
		service = serviceInventoryRepository.save(service);

		Integer sparesCategoryCount = serviceInventoryRepository.countByCategory(service.getCategory());
		ServiceCategory sparesCategory = serviceCategoryRepository.findByCategory(service.getCategory());
		sparesCategory.setCount(sparesCategoryCount);
		serviceCategoryRepository.save(sparesCategory);

		if (oldServiceCategory != null) {
			Integer oldServiceCategoryCount = serviceInventoryRepository.countByCategory(oldServiceCategory);
			ServiceCategory oldSparesCat = serviceCategoryRepository.findByCategory(oldServiceCategory);
			oldSparesCat.setCount(oldServiceCategoryCount);
			serviceCategoryRepository.save(oldSparesCat);
		}

		return service;
	}

	public List<ServiceInventory> findServiceInventoryWithFilter(ServiceFilter serviceFilter) {
		return serviceInventoryRepository.findServiceInventoryWithFilter(serviceFilter.categoryList(),
				serviceFilter.desc());
	}

	public List<?> findAllServiceCategory() {
		return serviceCategoryRepository.findAll();
	}

	public ServiceCategory saveServiceCategory(ServiceCategory serviceCategory) throws Exception {
		ServiceCategory category = serviceCategoryRepository.findByCategory(serviceCategory.getCategory());
		if (category == null)
			return serviceCategoryRepository.save(serviceCategory);
		else {
			throw new Exception(serviceCategory.getCategory() + " is already available as ServiceCategory");
		}
	}

	public synchronized ServiceCategory deleteServiceCategoryById(String id) throws Exception {
		ServiceCategory serviceCategory = serviceCategoryRepository.findById(id).orElse(null);

		if (serviceCategory != null) {
			// sparesInventoryRepository.updateCategory(sparesCategory.getCategory(), "");
			if (serviceCategory.getCount() != null && serviceCategory.getCount() > 0)
				throw new Exception(
						"Cannot delete category as its has " + serviceCategory.getCount() + " Services reffering to ");
		} else {
			throw new Exception("Invalid id for deleteServiceCategoryById " + id);
		}

		serviceCategoryRepository.deleteById(id);
		return serviceCategory;
	}

	public ServiceCategory updateServiceCategory(String oldCategory, String newCategory) {
		ServiceCategory serviceCategory = serviceCategoryRepository.findByCategory(oldCategory);
		if (serviceCategory != null) {
			serviceCategory.setCategory(newCategory);
			serviceCategory = serviceCategoryRepository.save(serviceCategory);
			serviceInventoryRepository.updateCategory(oldCategory, newCategory);
		}
		return serviceCategory;
	}

}

package com.sas.carwash.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sas.carwash.entity.ServiceCategory;
import com.sas.carwash.entity.ServiceInventory;
import com.sas.carwash.model.ServiceFilter;
import com.sas.carwash.service.ServiceInventoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/service")
@RequiredArgsConstructor
@Slf4j
public class ServiceInventoryController {

	private final ServiceInventoryService serviceInventoryService;

	@GetMapping
	public List<?> findAll() {
		return serviceInventoryService.findAll();
	}

	@PostMapping
	public ResponseEntity<?> save(@RequestBody ServiceInventory service) {
		try {
			return ResponseEntity.ok().body(serviceInventoryService.save(service));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/findServiceInventoryWithFilter")
	public List<ServiceInventory> findServiceInventoryWithFilter(@RequestBody ServiceFilter serviceFilter) {
		return serviceInventoryService.findServiceInventoryWithFilter(serviceFilter);
	}

	@GetMapping("/serviceCategory")
	public List<?> findAllServiceCategory() {
		return serviceInventoryService.findAllServiceCategory();
	}

	@PostMapping("/saveServiceCategory")
	public ResponseEntity<?> saveServiceCategory(@RequestBody ServiceCategory serviceCategory) {
		try {
			return ResponseEntity.ok().body(serviceInventoryService.saveServiceCategory(serviceCategory));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@DeleteMapping("/serviceCategory/{id}")
	public ResponseEntity<?> deleteServiceCategory(@PathVariable String id) {
		try {
			return ResponseEntity.ok().body(serviceInventoryService.deleteServiceCategoryById(id));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/serviceCategory/{oldCategory}/{newCategory}")
	public ResponseEntity<?> updateServiceCategory(@PathVariable String oldCategory, @PathVariable String newCategory) {
		try {
			return ResponseEntity.ok().body(serviceInventoryService.updateServiceCategory(oldCategory, newCategory));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}

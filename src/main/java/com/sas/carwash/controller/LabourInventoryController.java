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

import com.sas.carwash.entity.LabourCategory;
import com.sas.carwash.entity.LabourInventory;
import com.sas.carwash.model.LabourFilter;
import com.sas.carwash.service.LabourInventoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/labour")
@RequiredArgsConstructor
@Slf4j
public class LabourInventoryController {

	private final LabourInventoryService labourInventoryService;

	@GetMapping
	public List<?> findAll() {
		return labourInventoryService.findAll();
	}

	@PostMapping
	public ResponseEntity<?> save(@RequestBody LabourInventory labour) {
		try {
			return ResponseEntity.ok().body(labourInventoryService.save(labour));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/findLabourInventoryWithFilter")
	public List<LabourInventory> findLabourInventoryWithFilter(@RequestBody LabourFilter labourFilter) {
		return labourInventoryService.findLabourInventoryWithFilter(labourFilter);
	}

	@GetMapping("/labourCategory")
	public List<?> findAllLabourCategory() {
		return labourInventoryService.findAllLabourCategory();
	}

	@PostMapping("/saveLabourCategory")
	public ResponseEntity<?> saveLabourCategory(@RequestBody LabourCategory labourCategory) {
		try {
			return ResponseEntity.ok().body(labourInventoryService.saveLabourCategory(labourCategory));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@DeleteMapping("/labourCategory/{id}")
	public ResponseEntity<?> deleteLabourCategory(@PathVariable String id) {
		try {
			return ResponseEntity.ok().body(labourInventoryService.deleteLabourCategoryById(id));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/labourCategory/{oldCategory}/{newCategory}")
	public ResponseEntity<?> updateLabourCategory(@PathVariable String oldCategory, @PathVariable String newCategory) {
		try {
			return ResponseEntity.ok().body(labourInventoryService.updateLabourCategory(oldCategory, newCategory));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}

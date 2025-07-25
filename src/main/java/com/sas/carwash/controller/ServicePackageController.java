package com.sas.carwash.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sas.carwash.entity.ServicePackage;
import com.sas.carwash.service.ServicePackageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/package")
@RequiredArgsConstructor
@Slf4j
public class ServicePackageController {

    private final ServicePackageService packageService;
	
	@GetMapping
	public List<?> findAll() {
		return packageService.findAll();
	}

    @GetMapping("/{id}")
	public ServicePackage findById(@PathVariable String id) {
		return packageService.findById(id);
	}

	@GetMapping("/{phone}/{status}")
	public ServicePackage findFirstByPhoneAndStatusOrderByIdDesc(@PathVariable String phone, @PathVariable String status) {
		return packageService.findFirstByPhoneAndStatusOrderByIdDesc(phone, status);
	}

	@PostMapping
	public ServicePackage save(ServicePackage servicePackage) {
		return packageService.save(servicePackage);
	}

}

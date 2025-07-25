package com.sas.carwash.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sas.carwash.entity.ServicePackage;
import com.sas.carwash.repository.ServicePackageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServicePackageService {

	private final ServicePackageRepository packageRepository;

	public ServicePackage save(ServicePackage incomingPackage) {
		// Check if there's an existing OPEN package for the phone number
		ServicePackage existingOpenPackage = packageRepository
				.findFirstByPhoneAndStatusOrderByIdDesc(incomingPackage.getPhone(), "OPEN");

		if (existingOpenPackage != null) {
			// Top up the existing package
			BigDecimal newAmount = existingOpenPackage.getAmount().add(incomingPackage.getAmount());
			existingOpenPackage.setAmount(newAmount);

			// Optional: update name/date if you want latest info reflected
			existingOpenPackage.setDate(LocalDate.now());
			if (incomingPackage.getName() != null) {
				existingOpenPackage.setName(incomingPackage.getName());
			}

			return packageRepository.save(existingOpenPackage);
		} else {
			// No OPEN package exists, save new one
			incomingPackage.setStatus("OPEN");
			incomingPackage.setDate(LocalDate.now());
			if (incomingPackage.getJobIdToDeductedAmount() == null)
				incomingPackage.setJobIdToDeductedAmount(new HashMap<>());

			return packageRepository.save(incomingPackage);
		}
	}

	public ServicePackage update(ServicePackage servicePackage) {
		return packageRepository.save(servicePackage);
	}

	public ServicePackage findFirstByPhoneAndStatusOrderByIdDesc(String phone, String status) {
		return packageRepository.findFirstByPhoneAndStatusOrderByIdDesc(phone, status);
	}

	public ServicePackage findById(String id) {
		return packageRepository.findById(id).orElseThrow(() -> new RuntimeException("Package ID not found " + id));
	}

	public List<ServicePackage> findAll() {
		return packageRepository.findAllByOrderByIdDesc();
	}

	public List<ServicePackage> findByPhoneAndStatusOrderByIdDesc(String phone, String status) {
		return packageRepository.findByPhoneAndStatusOrderByIdDesc(phone, status);
	}

}

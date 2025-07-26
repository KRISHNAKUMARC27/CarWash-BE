package com.sas.carwash.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sas.carwash.entity.Payments;
import com.sas.carwash.entity.ServicePackage;
import com.sas.carwash.repository.ServicePackageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServicePackageService {

	private final ServicePackageRepository packageRepository;
	private final PaymentsService paymentsService;

	public ServicePackage save(ServicePackage incomingPackage) {
		boolean isUpdateRequest = incomingPackage.getId() != null;

		if (!isUpdateRequest) {
			// Always set new packages to OPEN status
			incomingPackage.setStatus("OPEN");
			incomingPackage.setDate(LocalDate.now());
			incomingPackage.setCreationDate(LocalDate.now());
			if (incomingPackage.getJobIdToDeductedAmount() == null) {
				incomingPackage.setJobIdToDeductedAmount(new HashMap<>());
			}

			// Check if there's an existing OPEN package for the phone number
			ServicePackage existingOpenPackage = packageRepository
					.findFirstByPhoneAndStatusOrderByIdDesc(incomingPackage.getPhone(), "OPEN");

			if (existingOpenPackage != null) {
				// Top up the existing package
				BigDecimal newAmount = existingOpenPackage.getAmount().add(incomingPackage.getAmount());
				existingOpenPackage.setAmount(newAmount);
				existingOpenPackage.setDate(LocalDate.now());

				if (incomingPackage.getCustomerName() != null) {
					existingOpenPackage.setCustomerName(incomingPackage.getCustomerName());
				}
				if (incomingPackage.getPaymentMode() != null) {
					existingOpenPackage.setPaymentMode(incomingPackage.getPaymentMode());
				}
				// update payment service
				informPaymentService(incomingPackage, "TOPUP");
				return packageRepository.save(existingOpenPackage);
			}
			// update payment service
			informPaymentService(incomingPackage, "NEW");

			return packageRepository.save(incomingPackage);

		} else {
			// Update existing package
			Optional<ServicePackage> optionalExisting = packageRepository.findById(incomingPackage.getId());
			if (optionalExisting.isPresent()) {
				ServicePackage existing = optionalExisting.get();

				if (incomingPackage.getAmount().compareTo(existing.getAmount()) != 0) {
					throw new IllegalArgumentException(
							"Amount cannot be changed. Please use the top-up process instead.");
				}

				existing.setCustomerName(incomingPackage.getCustomerName());
				existing.setDate(LocalDate.now());

				// Allow user to manually CLOSE the package
				if ("CLOSED".equalsIgnoreCase(incomingPackage.getStatus())) {
					existing.setStatus("CLOSED");
				}

				return packageRepository.save(existing);
			} else {
				throw new IllegalArgumentException(
						"Cannot update: package with id " + incomingPackage.getId() + " not found");
			}
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

	public void refundPackageAmount(String packageId, BigDecimal refundAmount, int jobId) {
		if (packageId == null || refundAmount == null || refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
			return; // Nothing to do
		}

		ServicePackage servicePackage = findById(packageId);
		if (servicePackage == null) {
			throw new IllegalStateException("ServicePackage not found for refund. ID: " + packageId);
		}

		// Step 1: Refund the amount back
		BigDecimal currentAmount = Optional.ofNullable(servicePackage.getAmount()).orElse(BigDecimal.ZERO);
		servicePackage.setAmount(currentAmount.add(refundAmount));

		// Step 2: Update the jobIdToDeductedAmount map
		Map<Integer, BigDecimal> jobMap = Optional.ofNullable(servicePackage.getJobIdToDeductedAmount())
				.orElse(new HashMap<>());

		BigDecimal existing = jobMap.getOrDefault(jobId, BigDecimal.ZERO);
		BigDecimal newAmount = existing.subtract(refundAmount);

		if (newAmount.compareTo(BigDecimal.ZERO) <= 0) {
			jobMap.remove(jobId); // Clean up if zero or negative
		} else {
			jobMap.put(jobId, newAmount);
		}

		servicePackage.setJobIdToDeductedAmount(jobMap);

		// Step 3: Persist
		update(servicePackage);
	}

	private void informPaymentService(ServicePackage incomingPackage, String info) {
		Payments payments = Payments.builder()
				.paymentAmount(incomingPackage
						.getAmount())
				.paymentMode(incomingPackage.getPaymentMode())
				.paymentDate(LocalDateTime.now())
				.category("PACKAGE")
				.info(info + " for Ph " + incomingPackage.getPhone())
				.build();
		paymentsService.save(payments);
	}

}

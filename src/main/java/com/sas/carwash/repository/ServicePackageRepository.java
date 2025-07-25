package com.sas.carwash.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.ServicePackage;

public interface ServicePackageRepository extends MongoRepository<ServicePackage, String> {

	List<ServicePackage> findAllByOrderByIdDesc();
	List<ServicePackage> findByPhoneAndStatusOrderByIdDesc(String phone, String status);
	ServicePackage findFirstByPhoneAndStatusOrderByIdDesc(String phone, String status);

	List<ServicePackage> findByDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
	List<ServicePackage> findByDate(LocalDateTime paymentDate);
	
}

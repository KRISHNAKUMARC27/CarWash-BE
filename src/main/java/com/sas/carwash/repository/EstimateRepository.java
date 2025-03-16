package com.sas.carwash.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.Estimate;

public interface EstimateRepository extends MongoRepository<Estimate, String> {
	
	List<Estimate> findAllByOrderByIdDesc();
	Estimate findByJobObjId(String jobObjId);
	List<Estimate> findByCreditFlagAndCreditSettledFlagOrderByIdDesc(Boolean creditFlag, Boolean creditSettledFlag);
	List<Estimate> findByBillCloseDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

}

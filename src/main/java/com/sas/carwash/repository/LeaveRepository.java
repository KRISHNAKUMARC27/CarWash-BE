package com.sas.carwash.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.Leave;

public interface LeaveRepository extends MongoRepository<Leave, String> {
	List<Leave> findByDate(LocalDate date);
	List<Leave> findAllByOrderByIdDesc();


}

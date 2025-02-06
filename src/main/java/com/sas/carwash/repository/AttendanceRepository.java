package com.sas.carwash.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.Attendance;

public interface AttendanceRepository extends MongoRepository<Attendance, String> {
	
	List<Attendance> findByDate(LocalDate date);
    
}

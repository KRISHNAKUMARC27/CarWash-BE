package com.sas.carwash.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.Attendance;

public interface AttendanceRepository extends MongoRepository<Attendance, String> {
    
}

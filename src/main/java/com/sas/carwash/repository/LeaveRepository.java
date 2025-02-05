package com.sas.carwash.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.Leave;

public interface LeaveRepository extends MongoRepository<Leave, String> {
    
}

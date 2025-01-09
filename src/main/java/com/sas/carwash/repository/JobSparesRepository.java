package com.sas.carwash.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.JobSpares;

public interface JobSparesRepository extends MongoRepository<JobSpares, String> {
    List<JobSpares> findByJobCloseDateBetween(LocalDateTime start, LocalDateTime end);
    List<JobSpares> findByJobCloseDateNotNull();

}

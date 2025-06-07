package com.sas.carwash.repository;

import java.util.Date;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.JobVehiclePhotos;

public interface JobVehiclePhotosRepository extends MongoRepository<JobVehiclePhotos, String> {

    void deleteByCreatedAtBefore(Date cutoff);
	
}

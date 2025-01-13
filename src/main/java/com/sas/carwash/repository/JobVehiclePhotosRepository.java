package com.sas.carwash.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.JobVehiclePhotos;

public interface JobVehiclePhotosRepository extends MongoRepository<JobVehiclePhotos, String> {
	
}

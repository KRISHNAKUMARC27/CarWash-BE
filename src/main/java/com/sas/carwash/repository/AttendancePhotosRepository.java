package com.sas.carwash.repository;

import java.util.Date;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.AttendancePhotos;

public interface AttendancePhotosRepository extends MongoRepository<AttendancePhotos, String> {

    void deleteByCreatedAtBefore(Date cutoff);
	
}

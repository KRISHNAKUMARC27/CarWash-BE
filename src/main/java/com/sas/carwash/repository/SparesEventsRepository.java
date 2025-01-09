package com.sas.carwash.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.SparesEvents;

public interface SparesEventsRepository extends MongoRepository<SparesEvents, String> {
	List<SparesEvents> findAllByOrderByIdDesc();

}

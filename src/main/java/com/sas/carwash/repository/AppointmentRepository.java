package com.sas.carwash.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sas.carwash.entity.Appointment;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {

}

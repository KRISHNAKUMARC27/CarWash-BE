package com.sas.carwash.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.sas.carwash.entity.Appointment;

import java.util.Date;
import java.util.List;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    List<Appointment> findByAppointmentDateTimeBetween(Date startDate, Date endDate);
}

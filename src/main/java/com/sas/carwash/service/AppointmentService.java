package com.sas.carwash.service;

import java.util.List;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.sas.carwash.entity.Appointment;
import com.sas.carwash.repository.AppointmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentService {

	private final AppointmentRepository appointmentRepository;

	public Appointment save(Appointment appointment) {
		// appointment.setStatus("SCHEDULED");
		return appointmentRepository.save(appointment);
	}

	public List<Appointment> getAllAppointments() {
		return appointmentRepository.findAllByOrderByIdDesc();
	}

	public Appointment getAppointmentById(String id) {
		return appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Appointment id not found"));
	}

	public List<Appointment> getAppointmentsBetweenDates(Date startDate, Date endDate) {
		return appointmentRepository.findByAppointmentDateTimeBetween(startDate, endDate);
	}
	
	
}

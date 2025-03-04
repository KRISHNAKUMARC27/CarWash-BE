package com.sas.carwash.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sas.carwash.entity.Appointment;
import com.sas.carwash.repository.AppointmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentService {

	private final AppointmentRepository appointmentRepository;

	public Appointment save(Appointment appointment) {
		return appointmentRepository.save(appointment);
	}

	public List<Appointment> getAllAppointments() {
		return appointmentRepository.findAll();
	}

	public Appointment getAppointmentById(String id) {
		return appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Appointment id not found"));
	}
	
	
}

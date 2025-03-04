package com.sas.carwash.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sas.carwash.entity.Appointment;
import com.sas.carwash.service.AppointmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

	private final AppointmentService appointmentService;

	@PostMapping()
	public Appointment createAppointment(@RequestBody Appointment appointment) {
		return appointmentService.save(appointment);
	}

	@GetMapping
	public List<Appointment> getAllAppointments() {
		return appointmentService.getAllAppointments();
	}

	@GetMapping("/{id}")
	public Appointment getAppointmentById(@PathVariable String id) {
		return appointmentService.getAppointmentById(id);
	}
}

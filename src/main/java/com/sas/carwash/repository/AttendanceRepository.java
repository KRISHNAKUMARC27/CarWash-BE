package com.sas.carwash.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.Attendance;

public interface AttendanceRepository extends MongoRepository<Attendance, String> {
	
	List<Attendance> findByDateAndCheckInTimeNotNull(LocalDate date);
	List<Attendance> findByDateAndLeaveTypeNotNull(LocalDate date);
	List<Attendance> findByDateAndCheckOutTimeNotNull(LocalDate date);
	List<Attendance> findByDate(LocalDate date);
	Attendance findByEmployeeIdAndDate(String employeeId, LocalDate date);
	List<Attendance> findAllByOrderByIdDesc();
	//List<Attendance> findByEmployeeId(String employeeId);
	List<Attendance> findByEmployeeIdAndDateBetween(String employeeId, LocalDate startDate, LocalDate endDate);

}

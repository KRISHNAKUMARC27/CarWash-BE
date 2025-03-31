package com.sas.carwash.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.sas.carwash.entity.Attendance;
import com.sas.carwash.repository.AttendanceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceScheduler {

    private final AttendanceRepository attendanceRepository;

    // Schedule to run every day at 11:55 PM
    @Scheduled(cron = "0 55 23 * * ?")
    public void updateIncompleteAttendance() {
        LocalDate today = LocalDate.now();
        List<Attendance> incompleteAttendances = attendanceRepository.findByDateAndCheckOutTimeIsNull(today);

        for (Attendance attendance : incompleteAttendances) {
            if (attendance.getCheckInTime() != null) {
                attendance.setCheckOutTime(LocalTime.of(20, 0)); // Set checkOutTime to 8 PM
                attendance.setWorkingHours(calculateWorkingHours(attendance.getCheckInTime(), attendance.getCheckOutTime()));
                attendanceRepository.save(attendance);
            }
        }
    }

    private Integer calculateWorkingHours(LocalTime checkIn, LocalTime checkOut) {
        if (checkIn != null && checkOut != null) {
            long hours = Duration.between(checkIn, checkOut).toHours();
            return (int) hours; // Convert to Integer
        }
        return 0; // Default if values are missing
    }
} 
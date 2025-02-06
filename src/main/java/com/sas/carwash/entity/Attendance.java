package com.sas.carwash.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Attendance implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	private String employeeId; // Reference to Employee
	private String employeeName;
	private LocalDate date;
	private Boolean present; // true = Present, false = Absent
	private LocalTime checkInTime;
	private LocalTime checkOutTime;
	private Integer workingHours;
	private Boolean onLeave; // true if leave is taken
	private String leaveType;

}

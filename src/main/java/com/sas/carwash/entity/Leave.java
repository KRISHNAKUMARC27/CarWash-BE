package com.sas.carwash.entity;

import java.io.Serializable;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Leave implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    private String id;
    private String employeeId;
    private String employeeName;
    private LocalDate date;
   // private LocalDate endDate;
    private String leaveType; // Sick, Casual, Paid, etc.
    private String status; // Approved, Pending, Rejected


}

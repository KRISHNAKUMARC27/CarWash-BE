package com.sas.carwash.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Appointment implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	private String customerName;
    private String phone;
    private LocalDateTime appointmentDateTime;
    private String service;
    private String description;
    private String staffName;
    private String status;

}

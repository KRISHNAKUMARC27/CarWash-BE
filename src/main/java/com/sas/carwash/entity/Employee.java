package com.sas.carwash.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employee implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	private String name;
	private String phone;
	private String department;
	private String designation;
	private String salaryType;
	private BigDecimal salary;
	private String status;
	private Boolean hasCredit;
	private BigDecimal salaryAdvance;
	
}

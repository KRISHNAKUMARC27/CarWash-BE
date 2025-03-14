package com.sas.carwash.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
public class EmployeeSalary implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	private String empId;
	private String name;
	private LocalDateTime salaryDate;
	private String salaryType;
	private BigDecimal salaryEarned;
	private BigDecimal salaryAdvance;
	private BigDecimal salaryPaid;
	private String salarySettlementType;
	private String paymentMode;
}

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
public class Expense implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	private String type;
	private String desc;
	private String paymentMode;
	private BigDecimal expenseAmount;
	private LocalDateTime expenseDate;
	private String comment;
	
}

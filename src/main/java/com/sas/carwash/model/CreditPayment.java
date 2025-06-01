package com.sas.carwash.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditPayment {
	
	private BigDecimal amount;
	private LocalDateTime creditDate;
	private String paymentMode;
	private String comment;

	private String paymentId;
	private String flag; // M - modify , D - delete, A - add
}

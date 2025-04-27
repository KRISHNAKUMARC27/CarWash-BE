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
public class PaymentSplit {
	private BigDecimal paymentAmount;
	private String paymentMode;
	private LocalDateTime paymentDate;
}

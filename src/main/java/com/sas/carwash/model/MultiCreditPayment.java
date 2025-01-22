package com.sas.carwash.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MultiCreditPayment {
	
	private List<String> invoiceIds;
	private BigDecimal amount;
	private LocalDateTime creditDate;
	private String paymentMode;
	private String comment;

}

package com.sas.carwash.entity;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobSparesInfo {

	private String sparesId;
	private String category;
	private String sparesAndLabour;
	private BigDecimal qty;
	private BigDecimal rate;
	private BigDecimal discount;
	private BigDecimal amount;
	private BigDecimal gstPercentage;
	private BigDecimal gstAmount;
	private String action;
	private String units;
}

package com.sas.carwash.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiptEstimate implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private Integer receiptId;
	private List<Integer> estimateIdList;
	private String ownerName;
	private BigDecimal amount;
	private LocalDateTime receiptDate;
	private String paymentMode;
	private String comment;

}

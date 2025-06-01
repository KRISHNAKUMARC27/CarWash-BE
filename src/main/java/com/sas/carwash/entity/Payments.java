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
public class Payments implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

    private BigDecimal paymentAmount;
	private String paymentMode;
	private LocalDateTime paymentDate;
    private String category;   //INVOICE OR ESTIMATE
    private Integer categoryFieldId;  //invoice or estimate id corresponding to the payment
    private boolean isCreditPayment;  //this payment is for settling the credit ?? 
}

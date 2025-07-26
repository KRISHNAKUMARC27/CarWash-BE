package com.sas.carwash.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;

import com.sas.carwash.model.PaymentModification;

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
    private Boolean isCreditPayment;  //this payment is for settling the credit ?? 
	private Boolean isDeleted; //payment deleted due to jobcard modifications.
	//private String paymentModified; //this can during jobcard reopen and some entries are removed, modified. TODO
	private List<PaymentModification> modifiedPayments;
	private String info; //some placeholder
}

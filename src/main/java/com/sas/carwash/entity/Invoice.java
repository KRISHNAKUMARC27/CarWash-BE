package com.sas.carwash.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;

import com.sas.carwash.model.CreditPayment;
import com.sas.carwash.model.PaymentSplit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Invoice implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private Integer invoiceId;
	private String jobObjId;
	private List<CreditPayment> creditPaymentList;
	private List<PaymentSplit> paymentSplitList;
	private BigDecimal grandTotal;
	private BigDecimal pendingAmount;
	private Boolean creditFlag;
	private Boolean creditSettledFlag;
	private LocalDateTime billCloseDate;
	
	private Integer jobId;
	private String ownerName;
	private String ownerPhoneNumber;
	private String vehicleRegNo;
	private String vehicleName;

}

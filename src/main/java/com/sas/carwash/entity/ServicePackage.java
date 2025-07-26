package com.sas.carwash.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServicePackage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String customerName; 
    private String phone;  // this is the package key
    private BigDecimal amount;
    private String paymentMode;
    private LocalDate date;
    private LocalDate creationDate;
    private String status; //OPEN  CLOSED

    private Map<Integer, BigDecimal> jobIdToDeductedAmount; 

}

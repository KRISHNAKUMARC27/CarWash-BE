package com.sas.carwash.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceInventory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private String category;
	private String desc;
	private BigDecimal amount;
	private BigDecimal cgst;
	private BigDecimal sgst;
	
	private String misc1;
	private String misc2;
	private String misc3;

}

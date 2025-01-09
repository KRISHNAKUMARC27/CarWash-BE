package com.sas.carwash.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobCardInfo {

	private String complaints;
	private String completed;
	private String remarks;
}

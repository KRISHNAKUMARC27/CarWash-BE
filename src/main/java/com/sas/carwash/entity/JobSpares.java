package com.sas.carwash.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.annotation.Id;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobSpares {
	
	@Id
	private String id;
	private String estimateObjId;
	private String invoiceObjId;
	
	private Integer jobId;
	private LocalDateTime jobCloseDate;
	private List<JobSparesInfo> jobSparesInfo;
	private List<JobSparesInfo> jobServiceInfo;
	private List<JobSparesInfo> jobLabourInfo;
//	private List<JobSparesInfo> jobConsumablesInfo;
//	private List<JobSparesInfo> jobLaborInfo;
//	private List<JobSparesInfo> jobExternalWorkInfo;
	private BigDecimal totalSparesValue;
	private BigDecimal totalSparesValueWithGST;
	private BigDecimal totalServiceValue;
	private BigDecimal totalServiceValueWithGST;
	private BigDecimal totalLabourValue;
	private BigDecimal totalLabourValueWithGST;
//	private BigDecimal totalConsumablesValue;
//	private BigDecimal totalLabourValue;
//	private BigDecimal totalExternalWorkValue;
	private BigDecimal grandTotal;
	private BigDecimal grandTotalWithGST;

}

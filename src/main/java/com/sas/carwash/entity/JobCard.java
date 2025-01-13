package com.sas.carwash.entity;

import java.io.Serializable;
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
public class JobCard implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	private String estimateObjId;
	private String invoiceObjId;
	
	private Integer jobId;			
	private String jobStatus;
	private LocalDateTime jobCreationDate;
	private LocalDateTime jobCloseDate;
	private Boolean billGenerated;
	private String ownerName;
	private String ownerAddress;
	private String ownerPhoneNumber;
//	private String ownerEmailId;
	private String vehicleRegNo;
	private String vehicleName;
//	private String vehicleModel;
	private Integer kiloMeters;
	private Integer nextFreeCheckKms;
	private Integer nextServiceKms;
//	private String technicianName;
//	private String driver;
//	private String fuelPoints;

//	private LocalDateTime vehicleOutDate;
//	
//	private String cover;
//	private String glass;
//	private String dashboardAndTools;
//	private String spareWheel;
//	private String jackeyHandles;
//	private String toolKits;
//	private String penDrive;
//	private String wheelCap;
//	private String acGrills;
	
//	private List<JobCardInfo> jobInfo;

}

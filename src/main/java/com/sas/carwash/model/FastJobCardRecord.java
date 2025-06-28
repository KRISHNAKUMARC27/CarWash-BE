package com.sas.carwash.model;

import java.util.List;

import com.sas.carwash.entity.JobSparesInfo;

public record FastJobCardRecord(String ownerName,
        String ownerAddress,
        String ownerPhoneNumber,
        String vehicleRegNo,
        String vehicleName,
        Integer kiloMeters,
        String billType,
        String paymentMode,
        List<JobSparesInfo> jobSparesInfo,
        List<JobSparesInfo> jobServiceInfo) {

}

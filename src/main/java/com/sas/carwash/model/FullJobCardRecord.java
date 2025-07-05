package com.sas.carwash.model;

import java.util.List;

import com.sas.carwash.entity.JobCard;
import com.sas.carwash.entity.JobSparesInfo;

public record FullJobCardRecord(JobCard jobCard,
        List<JobSparesInfo> jobSparesInfo,
        List<JobSparesInfo> jobServiceInfo) {

}

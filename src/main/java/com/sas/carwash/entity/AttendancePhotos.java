package com.sas.carwash.entity;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttendancePhotos {

	@Id
    private String id;
	
    private String name;
    private byte[] content;
    private String contentType;

    @CreatedDate
    private Date createdAt;
}

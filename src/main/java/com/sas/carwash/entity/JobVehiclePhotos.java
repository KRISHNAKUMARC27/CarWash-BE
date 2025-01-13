package com.sas.carwash.entity;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobVehiclePhotos {

	@Id
    private String id;
	
    private String name;
    private byte[] content;
    private String contentType;
}

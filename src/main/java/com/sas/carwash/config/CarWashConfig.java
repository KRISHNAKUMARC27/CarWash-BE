package com.sas.carwash.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "config")
@Data
public class CarWashConfig {

	private List<String> paymentModes = new ArrayList<>();
}

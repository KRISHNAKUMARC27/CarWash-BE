package com.sas.carwash.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sas.carwash.config.CarWashConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
@Slf4j
public class ConfigController {
	
	private final CarWashConfig carWashConfig;

	@GetMapping("/paymentmodes")
	public ResponseEntity<?> getPaymentModes() {
		
		return ResponseEntity.ok().body(carWashConfig.getPaymentModes());
	}

}

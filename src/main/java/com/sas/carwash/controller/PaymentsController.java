package com.sas.carwash.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sas.carwash.service.PaymentsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentsController {
    private final PaymentsService paymentsService;
	
	@GetMapping
	public List<?> findAll() {
		return paymentsService.findAll();
	}
}

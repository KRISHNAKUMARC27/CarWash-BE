package com.sas.carwash.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	//REPORTING
	@GetMapping("/report/daily/{date}")
	public Map<String, Object> getDailyPayments(@PathVariable String date) {
		return paymentsService.getDailyPayments(LocalDate.parse(date));
	}

	@GetMapping("/report/weekly/{year}/{week}")
	public Map<String, Object> getWeeklyPayments(@PathVariable int year, @PathVariable int week) {
		return paymentsService.getWeeklyPayments(year, week);
	}

	@GetMapping("/report/monthly/{year}/{month}")
	public Map<String, Object> getMonthlyPayments(@PathVariable int year, @PathVariable int month) {
		return paymentsService.getMonthlyPayments(year, month);
	}

	@GetMapping("/report/yearly/{year}")
	public Map<String, Object> getYearlyPayments(@PathVariable int year) {
		return paymentsService.getYearlyPayments(year);
	}

	@GetMapping("/report/daterange")
	public Map<String, Object> getPaymentsByDateRange(@RequestParam String startDate, @RequestParam String endDate) {
		return paymentsService.getPaymentsByDateRange(LocalDate.parse(startDate), LocalDate.parse(endDate));
	}
}

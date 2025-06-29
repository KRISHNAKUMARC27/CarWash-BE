package com.sas.carwash.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sas.carwash.service.CustomersStatsService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/customerStats")
@RequiredArgsConstructor
public class CustomersStatsController {

    private final CustomersStatsService customersStatsService;

    	//REPORTING
	@GetMapping("/report/daily/{date}")
	public Map<String, Object> getDailyCustomerStats(@PathVariable String date) {
		return customersStatsService.getDailyCustomerStats(LocalDate.parse(date));
	}

	@GetMapping("/report/weekly/{year}/{week}")
	public Map<String, Object> getWeeklyCustomerStats(@PathVariable int year, @PathVariable int week) {
		return customersStatsService.getWeeklyCustomerStats(year, week);
	}

	@GetMapping("/report/monthly/{year}/{month}")
	public Map<String, Object> getMonthlyCustomerStats(@PathVariable int year, @PathVariable int month) {
		return customersStatsService.getMonthlyCustomerStats(year, month);
	}

	@GetMapping("/report/yearly/{year}")
	public Map<String, Object> getYearlyCustomerStats(@PathVariable int year) {
		return customersStatsService.getYearlyCustomerStats(year);
	}

	@GetMapping("/report/daterange")
	public Map<String, Object> getCustomerStatsByDateRange(@RequestParam String startDate, @RequestParam String endDate) {
		return customersStatsService.getCustomerStatsByDateRange(LocalDate.parse(startDate), LocalDate.parse(endDate));
	}
    
}

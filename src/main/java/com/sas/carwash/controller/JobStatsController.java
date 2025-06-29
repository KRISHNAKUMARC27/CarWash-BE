package com.sas.carwash.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sas.carwash.service.JobStatsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/jobStats")
@RequiredArgsConstructor
public class JobStatsController {

    private final JobStatsService jobStatsService;

    	//REPORTING
	@GetMapping("/service/report/daily/{date}")
	public Map<String, Object> getDailyJobServiceStats(@PathVariable String date) {
		return jobStatsService.getDailyJobServiceStats(LocalDate.parse(date));
	}

	@GetMapping("/service/report/weekly/{year}/{week}")
	public Map<String, Object> getWeeklyJobServiceStats(@PathVariable int year, @PathVariable int week) {
		return jobStatsService.getWeeklyJobServiceStats(year, week);
	}

	@GetMapping("/service/report/monthly/{year}/{month}")
	public Map<String, Object> getMonthlyExpense(@PathVariable int year, @PathVariable int month) {
		return jobStatsService.getMonthlyJobServiceStats(year, month);
	}

	@GetMapping("/service/report/yearly/{year}")
	public Map<String, Object> getYearlyExpense(@PathVariable int year) {
		return jobStatsService.getYearlyJobServiceStats(year);
	}

	@GetMapping("/service/report/daterange")
	public Map<String, Object> getJobServiceStatsByDateRange(@RequestParam String startDate, @RequestParam String endDate) {
		return jobStatsService.getJobServiceStatsByDateRange(LocalDate.parse(startDate), LocalDate.parse(endDate));
	}
    
}

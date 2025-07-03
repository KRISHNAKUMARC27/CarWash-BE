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

	// REPORTING
	@GetMapping("/service/report/daily/{date}")
	public Map<String, Object> getDailyJobServiceStats(@PathVariable String date) {
		return jobStatsService.getDailyJobServiceStats(LocalDate.parse(date));
	}

	@GetMapping("/service/report/weekly/{year}/{week}")
	public Map<String, Object> getWeeklyJobServiceStats(@PathVariable int year, @PathVariable int week) {
		return jobStatsService.getWeeklyJobServiceStats(year, week);
	}

	@GetMapping("/service/report/monthly/{year}/{month}")
	public Map<String, Object> getMonthlyJobServiceStats(@PathVariable int year, @PathVariable int month) {
		return jobStatsService.getMonthlyJobServiceStats(year, month);
	}

	@GetMapping("/service/report/yearly/{year}")
	public Map<String, Object> getYearlyJobServiceStats(@PathVariable int year) {
		return jobStatsService.getYearlyJobServiceStats(year);
	}

	@GetMapping("/service/report/daterange")
	public Map<String, Object> getJobServiceStatsByDateRange(@RequestParam String startDate,
			@RequestParam String endDate) {
		return jobStatsService.getJobServiceStatsByDateRange(LocalDate.parse(startDate), LocalDate.parse(endDate));
	}

	// SPARES Stats
	@GetMapping("/spares/report/daily/{date}")
	public Map<String, Object> getDailyJobSparesStats(@PathVariable String date) {
		return jobStatsService.getDailyJobSparesStats(LocalDate.parse(date));
	}

	@GetMapping("/spares/report/weekly/{year}/{week}")
	public Map<String, Object> getWeeklyJobSparesStats(@PathVariable int year, @PathVariable int week) {
		return jobStatsService.getWeeklyJobSparesStats(year, week);
	}

	@GetMapping("/spares/report/monthly/{year}/{month}")
	public Map<String, Object> getMonthlyJobSparesStats(@PathVariable int year, @PathVariable int month) {
		return jobStatsService.getMonthlyJobSparesStats(year, month);
	}

	@GetMapping("/spares/report/yearly/{year}")
	public Map<String, Object> getYearlyJobSparesStats(@PathVariable int year) {
		return jobStatsService.getYearlyJobSparesStats(year);
	}

	@GetMapping("/spares/report/daterange")
	public Map<String, Object> getJobSparesStatsByDateRange(@RequestParam String startDate,
			@RequestParam String endDate) {
		return jobStatsService.getJobSparesStatsByDateRange(LocalDate.parse(startDate), LocalDate.parse(endDate));
	}

	// Labour Stats
	@GetMapping("/labour/report/daily/{date}")
	public Map<String, Object> getDailyJobLabourStats(@PathVariable String date) {
		return jobStatsService.getDailyJobLabourStats(LocalDate.parse(date));
	}

	@GetMapping("/labour/report/weekly/{year}/{week}")
	public Map<String, Object> getWeeklyJobLabourStats(@PathVariable int year, @PathVariable int week) {
		return jobStatsService.getWeeklyJobLabourStats(year, week);
	}

	@GetMapping("/labour/report/monthly/{year}/{month}")
	public Map<String, Object> getMonthlyJobLabourStats(@PathVariable int year, @PathVariable int month) {
		return jobStatsService.getMonthlyJobLabourStats(year, month);
	}

	@GetMapping("/labour/report/yearly/{year}")
	public Map<String, Object> getYearlyJobLabourStats(@PathVariable int year) {
		return jobStatsService.getYearlyJobLabourStats(year);
	}

	@GetMapping("/labour/report/daterange")
	public Map<String, Object> getJobLabourStatsByDateRange(@RequestParam String startDate,
			@RequestParam String endDate) {
		return jobStatsService.getJobLabourStatsByDateRange(LocalDate.parse(startDate), LocalDate.parse(endDate));
	}

}

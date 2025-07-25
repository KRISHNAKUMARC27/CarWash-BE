package com.sas.carwash.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sas.carwash.entity.Attendance;
import com.sas.carwash.entity.AttendancePhotos;
import com.sas.carwash.entity.Department;
import com.sas.carwash.entity.Employee;
import com.sas.carwash.entity.EmployeeSalary;
import com.sas.carwash.model.AttendanceRecord;
import com.sas.carwash.service.EmployeeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

	private final EmployeeService employeeService;

	@GetMapping
	public List<?> findAll() {
		return employeeService.findAllEmployee();
	}

	@PostMapping
	public Employee save(@RequestBody Employee employee) {
		return employeeService.saveEmployee(employee);
	}

	@GetMapping("/department")
	public List<?> findAllDepartment() {
		return employeeService.findAllDepartment();
	}

	@PostMapping("/department")
	public ResponseEntity<?> saveDepartment(@RequestBody Department department) {
		try {
			return ResponseEntity.ok().body(employeeService.saveDepartment(department));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@DeleteMapping("/department/{id}")
	public ResponseEntity<?> deleteDepartmentById(@PathVariable String id) {
		try {
			return ResponseEntity.ok().body(employeeService.deleteDepartmentById(id));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/department/{oldDept}/{newDept}")
	public ResponseEntity<?> updateDepartment(@PathVariable String oldDept, @PathVariable String newDept) {
		try {
			return ResponseEntity.ok().body(employeeService.updateDepartment(oldDept, newDept));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/attendance")
	public ResponseEntity<?> saveAttendance(@RequestBody AttendanceRecord attendance) {
		try {
			return ResponseEntity.ok().body(employeeService.saveAttendanceRecord(attendance));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/attendance")
	public ResponseEntity<?> updateAttendance(@RequestBody Attendance attendance) {
		try {
			return ResponseEntity.ok().body(employeeService.updateAttendanceRecord(attendance));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/attendance")
	public List<?> findAllAttendance() {
		return employeeService.findAllAttendance();
	}

	@GetMapping("/attendance/today")
	public List<?> findAllAttendaceToday() {
		return employeeService.findAllAttendaceToday();
	}

	@PostMapping("/attendance/uploadPhotos/{id}")
	public ResponseEntity<?> uploadPhotos(@RequestParam("file") MultipartFile zipFile, @PathVariable String id) {
		try {
			// Save the zip file to MongoDB
			return ResponseEntity.ok(employeeService.saveZipToMongo(zipFile, id));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error uploading photos: " + e.getMessage());
		}
	}

	@GetMapping("/attendance/getPhotos/{id}")
	public ResponseEntity<?> getPhotos(@PathVariable String id) {
		try {
			AttendancePhotos photoDoc = employeeService.getZipPhotos(id);
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(photoDoc.getContentType()))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + photoDoc.getName() + "\"")
					.body(photoDoc.getContent());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error uploading photos: " + e.getMessage());
		}
	}


	@GetMapping("/attendance/daily/{date}")
	public Map<String, Object> getDailyAttendance(@PathVariable String date) {
		return employeeService.getDailyAttendance(LocalDate.parse(date));
	}

	@GetMapping("/attendance/weekly/{year}/{week}")
	public Map<String, Object> getWeeklyAttendance(@PathVariable int year, @PathVariable int week) {
		return employeeService.getWeeklyAttendance(year, week);
	}

	@GetMapping("/attendance/monthly/{year}/{month}")
	public Map<String, Object> getMonthlyAttendance(@PathVariable int year, @PathVariable int month) {
		return employeeService.getMonthlyAttendance(year, month);
	}

	@GetMapping("/attendance/yearly/{year}")
	public Map<String, Object> getYearlyAttendance(@PathVariable int year) {
		return employeeService.getYearlyAttendance(year);
	}

	@GetMapping("/attendance/daterange")
	public Map<String, Object> getAttendanceByDateRange(@RequestParam String startDate, @RequestParam String endDate) {
		return employeeService.getAttendanceByDateRange(LocalDate.parse(startDate), LocalDate.parse(endDate));
	}

	@GetMapping("/attendance/day/daily/{date}")
	public Map<String, Object> getDayWiseDailyAttendance(@PathVariable String date) {
		return employeeService.getDayWiseDailyAttendance(LocalDate.parse(date));
	}

	@GetMapping("/attendance/day/weekly/{year}/{week}")
	public Map<String, Object> getDayWiseWeeklyAttendance(@PathVariable int year, @PathVariable int week) {
		return employeeService.getDayWiseWeeklyAttendance(year, week);
	}

	@GetMapping("/attendance/day/monthly/{year}/{month}")
	public Map<String, Object> getDayWiseMonthlyAttendance(@PathVariable int year, @PathVariable int month) {
		return employeeService.getDayWiseMonthlyAttendance(year, month);
	}

	@GetMapping("/attendance/day/yearly/{year}")
	public Map<String, Object> getDayWiseYearlyAttendance(@PathVariable int year) {
		return employeeService.getDayWiseYearlyAttendance(year);
	}

	@GetMapping("/attendance/day/daterange")
	public Map<String, Object> getDayWiseAttendanceByDateRange(@RequestParam String startDate, @RequestParam String endDate) {
		return employeeService.getDayWiseAttendanceByDateRange(LocalDate.parse(startDate), LocalDate.parse(endDate));
	}

	@GetMapping("/leave")
	public List<?> findAllLeave() {
		return employeeService.findAllLeave();
	}

	@GetMapping("/leave/daily/{date}")
	public Map<String, Object> getDailyLeave(@PathVariable String date) {
		return employeeService.getDailyLeave(LocalDate.parse(date));
	}

	@GetMapping("/leave/weekly/{year}/{week}")
	public Map<String, Object> getWeeklyLeave(@PathVariable int year, @PathVariable int week) {
		return employeeService.getWeeklyLeave(year, week);
	}

	@GetMapping("/leave/monthly/{year}/{month}")
	public Map<String, Object> getMonthlyLeave(@PathVariable int year, @PathVariable int month) {
		return employeeService.getMonthlyLeave(year, month);
	}

	@GetMapping("/leave/yearly/{year}")
	public Map<String, Object> getYearlyLeave(@PathVariable int year) {
		return employeeService.getYearlyLeave(year);
	}

	@GetMapping("/leave/daterange")
	public Map<String, Object> getLeaveByDateRange(@RequestParam String startDate, @RequestParam String endDate) {
		return employeeService.getLeaveByDateRange(LocalDate.parse(startDate), LocalDate.parse(endDate));
	}

	@PostMapping("/settleEmployeeSalary")
	public ResponseEntity<?> settleEmployeeSalary(@RequestBody EmployeeSalary empSalary) {
		try {
			return ResponseEntity.ok().body(employeeService.settleEmployeeSalary(empSalary));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/setupEmployeeSalary/{id}/{salaryDate}")
	public ResponseEntity<?> setupEmployeeSalary(@PathVariable String id, @PathVariable String salaryDate) {
		try {
			return ResponseEntity.ok().body(employeeService.setupEmployeeSalary(id, LocalDate.parse(salaryDate)));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}

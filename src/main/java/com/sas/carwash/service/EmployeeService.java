package com.sas.carwash.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sas.carwash.entity.Attendance;
import com.sas.carwash.entity.AttendancePhotos;
import com.sas.carwash.entity.Department;
import com.sas.carwash.entity.Employee;
import com.sas.carwash.entity.EmployeeSalary;
import com.sas.carwash.entity.Leave;
import com.sas.carwash.model.AttendanceRecord;
import com.sas.carwash.repository.AttendancePhotosRepository;
import com.sas.carwash.repository.AttendanceRepository;
import com.sas.carwash.repository.DepartmentRepository;
import com.sas.carwash.repository.EmployeeRepository;
import com.sas.carwash.repository.EmployeeSalaryRepository;
import com.sas.carwash.repository.LeaveRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

	private final EmployeeRepository employeeRepository;
	private final AttendanceRepository attendanceRepository;
	private final LeaveRepository leaveRepository;
	private final DepartmentRepository departmentRepository;
	private final EmployeeSalaryRepository employeeSalaryRepository;
	private final AttendancePhotosRepository attendancePhotosRepository;

	private final ExpenseService expenseService;

	private final MongoTemplate mongoTemplate;

	public List<?> findAllEmployee() {
		return employeeRepository.findAll();
	}

	public Employee saveEmployee(Employee employee) {
		String oldEmployeeDept = null;
		if (employee.getId() != null) {
			// the employee is getting updated.
			Employee oldEmployee = employeeRepository.findById(employee.getId()).orElse(null);
			if (oldEmployee != null && !oldEmployee.getDepartment().equals(employee.getDepartment())) {
				oldEmployeeDept = oldEmployee.getDepartment();
			}
		}

		employee = employeeRepository.save(employee);

		Integer empDeptCount = employeeRepository.countByDepartment(employee.getDepartment());
		Department department = departmentRepository.findByDepartmentName(employee.getDepartment());
		department.setCount(empDeptCount);
		departmentRepository.save(department);

		if (oldEmployeeDept != null) {
			Integer oldDeptCount = employeeRepository.countByDepartment(oldEmployeeDept);
			Department oldDept = departmentRepository.findByDepartmentName(oldEmployeeDept);
			oldDept.setCount(oldDeptCount);
			departmentRepository.save(oldDept);
		}
		return employee;
	}

	public Employee findByIdEmployee(String id) {
		return employeeRepository.findById(id).orElseThrow(() -> {
			throw new RuntimeException("Employee id not found");
		});
	}

	public List<?> findAllDepartment() {
		return departmentRepository.findAll();
	}

	public Department saveDepartment(Department dept) {
		return departmentRepository.save(dept);
	}

	public synchronized Department deleteDepartmentById(String id) throws Exception {
		Department department = departmentRepository.findById(id).orElse(null);

		if (department != null) {
			if (department.getCount() != null && department.getCount() > 0)
				throw new Exception(
						"Cannot delete department as its has " + department.getCount() + " Department reffering to ");
		} else {
			throw new Exception("Invalid id for deleteDepartmentById " + id);
		}

		departmentRepository.deleteById(id);
		return department;
	}

	public Department updateDepartment(String oldDept, String newDept) {
		Department department = departmentRepository.findByDepartmentName(oldDept);
		if (department != null) {
			department.setDepartmentName(newDept);
			department = departmentRepository.save(department);
			Query query = new Query(Criteria.where("department").is(oldDept));
			Update update = new Update().set("department", newDept);
			mongoTemplate.updateMulti(query, update, Employee.class);
		}
		return department;
	}

	public Attendance saveAttendanceRecord(AttendanceRecord record) throws Exception {
		Employee employee = employeeRepository.findById(record.employeeId())
				.orElseThrow(() -> new RuntimeException("Employee not found"));

		// Fetch existing attendance record for the employee today
		Attendance existingAttendance = attendanceRepository.findByEmployeeIdAndDate(record.employeeId(),
				LocalDate.now());

		Attendance attendance = (existingAttendance != null) ? existingAttendance : Attendance.builder().build();
		attendance.setDate(LocalDate.now());
		attendance.setEmployeeId(record.employeeId());
		attendance.setEmployeeName(employee.getName());

		if (existingAttendance != null) {
			if (existingAttendance.getLeaveType() != null) {
				throw new Exception("Leave already recorded for the employee");
			} else {
				if (record.reason() != null) {
					// TODO .. check if employee can take leave after IN time.
					throw new Exception("Attendance already recorded for the employee");
				}
			}
		}
		if (record.reason() != null) {
			attendance.setLeaveType(record.reason());
			Leave leave = Leave.builder().date(LocalDate.now()).employeeId(attendance.getEmployeeId())
					.employeeName(attendance.getEmployeeName()).leaveType(attendance.getLeaveType()).build();
			leaveRepository.save(leave);
			attendance.setPresent(false);
			return attendanceRepository.save(attendance);
		}

		if (record.status().equals("IN")) {
			if (existingAttendance != null && existingAttendance.getCheckInTime() != null) {
				throw new Exception("IN time already recorded for the employee");
			}
			attendance.setCheckInTime(LocalTime.now());
			attendance.setPresent(true);
		} else if (record.status().equals("OUT")) {
			if (existingAttendance == null || existingAttendance.getCheckInTime() == null) {
				throw new Exception("Cannot mark OUT before IN time is recorded");
			}
			if (existingAttendance.getCheckOutTime() != null) {
				throw new Exception("OUT time already recorded for the employee");
			}
			attendance.setCheckOutTime(LocalTime.now());
			attendance
					.setWorkingHours(calculateWorkingHours(attendance.getCheckInTime(), attendance.getCheckOutTime()));
		} else {
			throw new Exception("Invalid attendance status");
		}

		return attendanceRepository.save(attendance);
	}

	public Attendance updateAttendanceRecord(Attendance attendance) {
		if (attendance.getLeaveType() != null) {
			// Handle leave case
			Leave leave = Leave.builder()
					.date(attendance.getDate())
					.employeeId(attendance.getEmployeeId())
					.employeeName(attendance.getEmployeeName())
					.leaveType(attendance.getLeaveType())
					.build();
			leaveRepository.save(leave);
			attendance.setOnLeave(true);
			attendance.setPresent(false);
			attendance.setCheckInTime(null);
			attendance.setCheckOutTime(null);
			attendance.setWorkingHours(0);
		} else {
			// Handle case where leave is cancelled and in/out times are provided
			attendance.setOnLeave(false);
			attendance.setPresent(true);

			// Delete the existing leave record if any
			leaveRepository.deleteByEmployeeIdAndDate(attendance.getEmployeeId(), attendance.getDate());

			// Optional: calculate working hours if both times are provided
			if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) {
				long hours = Duration.between(attendance.getCheckInTime(), attendance.getCheckOutTime()).toHours();
				attendance.setWorkingHours((int) hours);
			}
		}

		return attendanceRepository.save(attendance);
	}

	private Integer calculateWorkingHours(LocalTime checkIn, LocalTime checkOut) {
		if (checkIn != null && checkOut != null) {
			long hours = Duration.between(checkIn, checkOut).toHours();
			return (int) hours; // Convert to Integer
		}
		return 0; // Default if values are missing
	}

	public List<?> findAllAttendace() {

		return attendanceRepository.findAllByOrderByIdDesc();
	}

	public List<?> findAllAttendaceToday() {
		return attendanceRepository.findByDate(LocalDate.now());
	}

	public AttendancePhotos saveZipToMongo(MultipartFile zipFile, String id) throws IOException {

		AttendancePhotos photos = AttendancePhotos.builder().id(id).name(zipFile.getOriginalFilename())
				.content(zipFile.getBytes()).contentType(zipFile.getContentType()).build();
		return attendancePhotosRepository.save(photos);

	}

	public AttendancePhotos getZipPhotos(String id) {
		// TODO Auto-generated method stub
		return attendancePhotosRepository.findById(id).orElseThrow(() -> new RuntimeException("Photos not found for id " + id));
	}

	@Scheduled(cron = "0 0 3 * * ?") // Runs daily at 3 AM
	public void deleteOldPhotos() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -30);
		Date cutoff = calendar.getTime();

		attendancePhotosRepository.deleteByCreatedAtBefore(cutoff);
	}

	public Map<String, Object> getDailyAttendance(LocalDate date) {
		List<Attendance> records = attendanceRepository.findByDate(date);

		return processAttendance(records);
	}

	public Map<String, Object> getWeeklyAttendance(int year, int week) {
		List<Attendance> records = attendanceRepository.findAll().stream().filter(
				att -> att.getDate().getYear() == year && att.getDate().get(WeekFields.ISO.weekOfYear()) == week)
				.collect(Collectors.toList());

		return processAttendance(records);
	}

	public Map<String, Object> getMonthlyAttendance(int year, int month) {
		List<Attendance> records = attendanceRepository.findAll().stream()
				.filter(att -> att.getDate().getYear() == year && att.getDate().getMonthValue() == month)
				.collect(Collectors.toList());

		return processAttendance(records);
	}

	public Map<String, Object> getYearlyAttendance(int year) {
		List<Attendance> records = attendanceRepository.findAll().stream()
				.filter(att -> att.getDate().getYear() == year).collect(Collectors.toList());

		return processAttendance(records);
	}

	public Map<String, Object> getAttendanceByDateRange(LocalDate startDate, LocalDate endDate) {
		List<Attendance> records = attendanceRepository.findAll().stream()
				.filter(att -> !att.getDate().isBefore(startDate) && !att.getDate().isAfter(endDate))
				.collect(Collectors.toList());

		return processAttendance(records);
	}

	private Map<String, Object> processAttendance(List<Attendance> records) {
		Map<String, Object> result = new HashMap<>();

		long presentCount = records.stream().filter(Attendance::getPresent).count();
		long absentCount = records.size() - presentCount;

		Map<String, Integer> workingHoursPerEmployee = records.stream()
				.collect(Collectors.groupingBy(Attendance::getEmployeeName,
						Collectors.summingInt(att -> Optional.ofNullable(att.getWorkingHours()).orElse(0))));

		result.put("totalPresent", presentCount);
		result.put("totalAbsent", absentCount);
		result.put("workingHours", workingHoursPerEmployee);

		return result;
	}

	public Map<String, Object> getDailyLeave(LocalDate date) {
		List<Leave> records = leaveRepository.findByDate(date);

		return processLeave(records);
	}

	public Map<String, Object> getWeeklyLeave(int year, int week) {
		List<Leave> records = leaveRepository.findAll().stream().filter(
				att -> att.getDate().getYear() == year && att.getDate().get(WeekFields.ISO.weekOfYear()) == week)
				.collect(Collectors.toList());

		return processLeave(records);
	}

	public Map<String, Object> getMonthlyLeave(int year, int month) {
		List<Leave> records = leaveRepository.findAll().stream()
				.filter(att -> att.getDate().getYear() == year && att.getDate().getMonthValue() == month)
				.collect(Collectors.toList());

		return processLeave(records);
	}

	public Map<String, Object> getYearlyLeave(int year) {
		List<Leave> records = leaveRepository.findAll().stream().filter(att -> att.getDate().getYear() == year)
				.collect(Collectors.toList());

		return processLeave(records);
	}

	public Map<String, Object> getLeaveByDateRange(LocalDate startDate, LocalDate endDate) {
		List<Leave> records = leaveRepository.findAll().stream()
				.filter(att -> !att.getDate().isBefore(startDate) && !att.getDate().isAfter(endDate))
				.collect(Collectors.toList());

		return processLeave(records);
	}

	private Map<String, Object> processLeave(List<Leave> records) {
		Map<String, Object> result = new HashMap<>();

		long presentCount = records.size();

		Map<String, Long> leaveCountPerEmployee = records.stream()
				.collect(Collectors.groupingBy(Leave::getEmployeeName, Collectors.counting()));

		result.put("totalAbsent", presentCount);
		result.put("leaveCount", leaveCountPerEmployee);

		return result;
	}

	public List<?> findAllLeave() {
		// TODO Auto-generated method stub
		return leaveRepository.findAllByOrderByIdDesc();
	}

	private List<Leave> getEmpMonthlyLeave(String employeeId, int year, int month) {
		LocalDate start = LocalDate.of(year, month, 1);
		LocalDate end = start.withDayOfMonth(start.lengthOfMonth()); // Gets the last day of the month
		return leaveRepository.findByEmployeeIdAndDateBetween(employeeId, start, end);
	}

	private List<Leave> getEmpWeeklyLeave(String employeeId, LocalDate anyDateInWeek) {
		// Get the Monday of that week
		LocalDate weekStart = anyDateInWeek.with(DayOfWeek.MONDAY);
		LocalDate weekEnd = weekStart.plusDays(6); // Sunday

		return leaveRepository.findByEmployeeIdAndDateBetween(employeeId, weekStart, weekEnd);
	}

	private List<Attendance> getEmpMonthlyAttendance(String employeeId, int year, int month) {
		LocalDate start = LocalDate.of(year, month, 1);
		LocalDate end = start.withDayOfMonth(start.lengthOfMonth()); // Gets the last day of the month
		return attendanceRepository.findByEmployeeIdAndDateBetween(employeeId, start, end);
	}

	private List<Attendance> getEmpWeeklyAttendance(String employeeId, LocalDate anyDateInWeek) {
		// Get the Monday of that week
		LocalDate weekStart = anyDateInWeek.with(DayOfWeek.MONDAY);
		LocalDate weekEnd = weekStart.plusDays(6); // Sunday

		return attendanceRepository.findByEmployeeIdAndDateBetween(employeeId, weekStart, weekEnd);
	}

	private BigDecimal calculateHourlySalary(List<Attendance> attendance, BigDecimal hourlyRate) {
		int hoursWorked = attendance.stream().mapToInt(a -> a.getWorkingHours() != null ? a.getWorkingHours() : 0)
				.sum();
		return hourlyRate.multiply(BigDecimal.valueOf(hoursWorked));
	}

	// Constants for salary calculations
	final int FIXED_MONTH_DAYS = 30; // Assumed fixed days in a month for salary calculation
	final int PAID_DAYS_IN_WEEK = 7; // Number of days in a week
	final int WEEKS_IN_MONTH = 4; // Assumed weeks in a month for weekly salary calculation

	public EmployeeSalary setupEmployeeSalary(String id, LocalDate salaryDate) throws IllegalStateException {
		// Fetch employee details from the repository
		Employee employee = employeeRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Employee not found"));

		int year = salaryDate.getYear();
		int month = salaryDate.getMonthValue();

		BigDecimal salaryEarned = calculateSalary(employee, id, salaryDate, year, month);

		// Throw exception if salary could not be computed
		if (salaryEarned == null) {
			throw new IllegalStateException(
					"Salary could not be computed. Possibly due to unsupported type/settlement combo.");
		}

		// Build and return the EmployeeSalary object
		return EmployeeSalary.builder()
				.empId(id)
				.name(employee.getName())
				.salaryAdvance(employee.getSalaryAdvance())
				.salaryEarned(salaryEarned)
				.salarySettlementType(employee.getSalarySettlementType())
				.salaryType(employee.getSalaryType())
				.build();
	}

	private BigDecimal calculateSalary(Employee employee, String id, LocalDate salaryDate, int year, int month) {
		switch (employee.getSalaryType()) {
			case "MONTHLY":
				return calculateMonthlySalary(employee, id, year, month);
			case "WEEKLY":
				return calculateWeeklySalary(employee, id, salaryDate, year, month);
			case "DAILY":
				return calculateDailySalary(employee, id, salaryDate, year, month);
			case "HOURLY":
				return calculateHourlySalary(employee, id, salaryDate, year, month);
			default:
				return null;
		}
	}

	private BigDecimal calculateMonthlySalary(Employee employee, String id, int year, int month) {
		if (employee.getSalarySettlementType().equals("MONTHLY")) {
			List<Leave> leaveList = getEmpMonthlyLeave(id, year, month);
			BigDecimal dailyRate = employee.getSalary().divide(BigDecimal.valueOf(FIXED_MONTH_DAYS), 2,
					RoundingMode.HALF_UP);
			int leaveDays = leaveList.size();
			return employee.getSalary().subtract(dailyRate.multiply(BigDecimal.valueOf(leaveDays)));
		}
		return null;
	}

	private BigDecimal calculateWeeklySalary(Employee employee, String id, LocalDate salaryDate, int year, int month) {
		BigDecimal dailyRate = employee.getSalary().divide(BigDecimal.valueOf(PAID_DAYS_IN_WEEK), 2,
				RoundingMode.HALF_UP);
		if (employee.getSalarySettlementType().equals("MONTHLY")) {
			List<Leave> leaveList = getEmpMonthlyLeave(id, year, month);
			int leaveDays = leaveList.size();
			BigDecimal salaryEarned = employee.getSalary().multiply(BigDecimal.valueOf(WEEKS_IN_MONTH));
			return salaryEarned.subtract(dailyRate.multiply(BigDecimal.valueOf(leaveDays)));
		} else if (employee.getSalarySettlementType().equals("WEEKLY")) {
			List<Leave> leaveList = getEmpWeeklyLeave(id, salaryDate);
			int leaveDays = leaveList.size();
			return employee.getSalary().subtract(dailyRate.multiply(BigDecimal.valueOf(leaveDays)));
		}
		return null;
	}

	private BigDecimal calculateDailySalary(Employee employee, String id, LocalDate salaryDate, int year, int month) {
		if (employee.getSalarySettlementType().equals("MONTHLY")) {
			List<Leave> leaveList = getEmpMonthlyLeave(id, year, month);
			return employee.getSalary().multiply(BigDecimal.valueOf(FIXED_MONTH_DAYS - leaveList.size()));
		} else if (employee.getSalarySettlementType().equals("WEEKLY")) {
			List<Leave> leaveList = getEmpWeeklyLeave(id, salaryDate);
			return employee.getSalary().multiply(BigDecimal.valueOf(PAID_DAYS_IN_WEEK - leaveList.size()));
		} else if (employee.getSalarySettlementType().equals("DAILY")) {
			return employee.getSalary();
		}
		return null;
	}

	private BigDecimal calculateHourlySalary(Employee employee, String id, LocalDate salaryDate, int year, int month) {
		if (employee.getSalarySettlementType().equals("MONTHLY")) {
			List<Attendance> attendance = getEmpMonthlyAttendance(id, year, month);
			return calculateHourlySalary(attendance, employee.getSalary());
		} else if (employee.getSalarySettlementType().equals("WEEKLY")) {
			List<Attendance> attendance = getEmpWeeklyAttendance(id, salaryDate);
			return calculateHourlySalary(attendance, employee.getSalary());
		} else if (employee.getSalarySettlementType().equals("DAILY")) {
			Attendance attendance = attendanceRepository.findByEmployeeIdAndDate(id, salaryDate);
			if (attendance.getWorkingHours() != null) {
				return employee.getSalary().multiply(BigDecimal.valueOf(attendance.getWorkingHours()));
			}
		}
		return null;
	}

	public EmployeeSalary settleEmployeeSalary(EmployeeSalary empSalary) {
		// Determine date range based on settlement type
		LocalDateTime startDate;
		LocalDateTime endDate;

		LocalDate salaryLocalDate = empSalary.getSalaryDate().toLocalDate(); // Accepting salaryDate from frontend

		switch (empSalary.getSalarySettlementType()) {
			case "MONTHLY":
				startDate = salaryLocalDate.withDayOfMonth(1).atStartOfDay();
				endDate = salaryLocalDate.withDayOfMonth(salaryLocalDate.lengthOfMonth()).atTime(23, 59, 59);
				break;
			case "WEEKLY":
				startDate = salaryLocalDate.with(DayOfWeek.MONDAY).atStartOfDay();
				endDate = startDate.plusDays(6).with(LocalTime.MAX);
				break;
			case "DAILY":
				startDate = salaryLocalDate.atStartOfDay();
				endDate = salaryLocalDate.atTime(LocalTime.MAX);
				break;
			default:
				throw new IllegalArgumentException("Invalid salary settlement type");
		}

		// Check if salary is already settled in that range
		boolean alreadySettled = employeeSalaryRepository.existsByEmpIdAndSalaryDateBetween(
				empSalary.getEmpId(), startDate, endDate);

		if (alreadySettled) {
			throw new IllegalStateException("Salary already settled for this period.");
		}

		// Save salary & log expense
		empSalary.setSalaryDate(LocalDateTime.now());
		empSalary = employeeSalaryRepository.save(empSalary);

		// Record it as expense
		expenseService.saveSalaryExpense(empSalary);

		// calculate new salary advance and update it.
		Employee employee = employeeRepository.findById(empSalary.getEmpId())
				.orElseThrow(() -> new RuntimeException("Employee not found"));
		BigDecimal newSalaryAdvance = calculateNewSalaryAdvance(empSalary.getSalaryEarned(), empSalary.getSalaryPaid(),
				empSalary.getSalaryAdvance());
		employee.setSalaryAdvance(newSalaryAdvance);
		employeeRepository.save(employee);

		return empSalary;
	}

	public BigDecimal calculateNewSalaryAdvance(BigDecimal salaryEarned, BigDecimal salaryPaid, BigDecimal oldAdvance) {
		if (salaryEarned == null)
			salaryEarned = BigDecimal.ZERO;
		if (salaryPaid == null)
			salaryPaid = BigDecimal.ZERO;
		if (oldAdvance == null)
			oldAdvance = BigDecimal.ZERO;

		return oldAdvance.subtract(salaryEarned.subtract(salaryPaid));
	}

	public Map<String, Object> getDayWiseDailyAttendance(LocalDate date) {
		List<Attendance> records = attendanceRepository.findByDate(date);
		return dayWiseProcessAttendance(records);
	}

	public Map<String, Object> getDayWiseWeeklyAttendance(int year, int week) {
		List<Attendance> records = attendanceRepository.findAll().stream()
				.filter(att -> att.getDate().getYear() == year
						&& att.getDate().get(WeekFields.ISO.weekOfYear()) == week)
				.collect(Collectors.toList());
		return dayWiseProcessAttendance(records);
	}

	public Map<String, Object> getDayWiseMonthlyAttendance(int year, int month) {
		List<Attendance> records = attendanceRepository.findAll().stream()
				.filter(att -> att.getDate().getYear() == year && att.getDate().getMonthValue() == month)
				.collect(Collectors.toList());
		return dayWiseProcessAttendance(records);
	}

	public Map<String, Object> getDayWiseYearlyAttendance(int year) {
		List<Attendance> records = attendanceRepository.findAll().stream()
				.filter(att -> att.getDate().getYear() == year)
				.collect(Collectors.toList());
		return dayWiseProcessAttendance(records);
	}

	public Map<String, Object> getDayWiseAttendanceByDateRange(LocalDate startDate, LocalDate endDate) {
		List<Attendance> records = attendanceRepository.findAll().stream()
				.filter(att -> !att.getDate().isBefore(startDate) && !att.getDate().isAfter(endDate))
				.collect(Collectors.toList());
		return dayWiseProcessAttendance(records);
	}

	private Map<String, Object> dayWiseProcessAttendance(List<Attendance> records) {
		Map<String, Object> result = new HashMap<>();

		long totalPresent = records.stream().filter(Attendance::getPresent).count();
		long totalAbsent = records.size() - totalPresent;

		Map<String, Long> workingDaysPerEmployee = records.stream().filter(Attendance::getPresent)
				.collect(Collectors.groupingBy(Attendance::getEmployeeName, Collectors.counting()));

		result.put("totalPresent", totalPresent);
		result.put("totalAbsent", totalAbsent);
		result.put("workingDaysPerEmployee", workingDaysPerEmployee);

		return result;
	}

}

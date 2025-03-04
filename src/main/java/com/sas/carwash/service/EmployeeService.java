package com.sas.carwash.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.sas.carwash.entity.Attendance;
import com.sas.carwash.entity.Department;
import com.sas.carwash.entity.Employee;
import com.sas.carwash.entity.Leave;
import com.sas.carwash.model.AttendanceRecord;
import com.sas.carwash.repository.AttendanceRepository;
import com.sas.carwash.repository.DepartmentRepository;
import com.sas.carwash.repository.EmployeeRepository;
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

		if (record.reason() != null) {
			if (existingAttendance != null && existingAttendance.getLeaveType() != null) {
				throw new Exception("Leave already recorded for the employee");
			}

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

}

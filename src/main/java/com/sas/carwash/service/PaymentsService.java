package com.sas.carwash.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sas.carwash.entity.Payments;
import com.sas.carwash.model.PaymentModification;
import com.sas.carwash.repository.PaymentsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentsService {

	private final PaymentsRepository paymentsRepository;

	public Payments save(Payments payments) {
		return paymentsRepository.save(payments);
	}

	public Payments findById(String id) {
		return paymentsRepository.findById(id).orElseThrow(() -> new RuntimeException("Payments ID not found " + id));
	}

	public void deleteById(String id) {
		Payments currPayments = findById(id);
		currPayments = recordModifiedPayments(BigDecimal.ZERO, currPayments);
		currPayments.setIsDeleted(true);
		currPayments.setPaymentAmount(BigDecimal.ZERO);
		paymentsRepository.save(currPayments);
		//paymentsRepository.deleteById(id);
	}

	public List<Payments> findAll() {
		return paymentsRepository.findAllByOrderByIdDesc();
	}

	public Payments recordModifiedPayments(BigDecimal newAmount, Payments currPayments) {
		List<PaymentModification> modifiedPayments = new ArrayList<>(
				Optional.ofNullable(currPayments.getModifiedPayments()).orElse(Collections.emptyList()));
		PaymentModification payMod = PaymentModification.builder()
				.newAmount(newAmount)
				.oldAmount(currPayments.getPaymentAmount())
				.modifiedAt(LocalDateTime.now())
				.build();
		modifiedPayments.add(payMod);
		currPayments.setModifiedPayments(modifiedPayments);
		return currPayments;
	}

	// REPORT START
	private Map<String, Object> processPayments(List<Payments> records) {
		Map<String, Object> result = new HashMap<>();

		records = records.stream()
				.filter(p -> p.getIsDeleted() == null || !p.getIsDeleted()) // exclude only if true
				.collect(Collectors.toList());

		// Total Payments amount
		BigDecimal total = records.stream()
				.map(exp -> Optional.ofNullable(exp.getPaymentAmount()).orElse(BigDecimal.ZERO))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		result.put("total", total);

		// Group by type
		Map<String, BigDecimal> byType = records.stream()
				.collect(Collectors.groupingBy(exp -> Optional.ofNullable(exp.getCategory()).orElse("Unknown"),
						Collectors.reducing(BigDecimal.ZERO,
								exp -> Optional.ofNullable(exp.getPaymentAmount()).orElse(BigDecimal.ZERO),
								BigDecimal::add)));
		result.put("byType", byType);

		// Group by paymentMode
		Map<String, BigDecimal> byPaymentMode = records.stream()
				.collect(Collectors.groupingBy(exp -> Optional.ofNullable(exp.getPaymentMode()).orElse("Unknown"),
						Collectors.reducing(BigDecimal.ZERO,
								exp -> Optional.ofNullable(exp.getPaymentAmount()).orElse(BigDecimal.ZERO),
								BigDecimal::add)));
		result.put("byPaymentMode", byPaymentMode);

		Map<String, BigDecimal> isCredit = records.stream()
				.collect(Collectors.groupingBy(
						exp -> {
							Boolean credit = Optional.ofNullable(exp.getIsCreditPayment()).orElse(false);
							return credit ? "Credit" : "Non-Credit";
						},
						Collectors.reducing(
								BigDecimal.ZERO,
								exp -> Optional.ofNullable(exp.getPaymentAmount()).orElse(BigDecimal.ZERO),
								BigDecimal::add)));

		result.put("isCredit", isCredit);

		return result;
	}

	public Map<String, Object> getDailyPayments(LocalDate date) {
		LocalDateTime start = date.atStartOfDay();
		LocalDateTime end = start.plusDays(1);
		List<Payments> records = paymentsRepository.findByPaymentDateBetween(start, end);
		return processPayments(records);
	}

	public Map<String, Object> getWeeklyPayments(int year, int week) {
		List<Payments> records = paymentsRepository.findAll().stream().filter(e -> {
			LocalDateTime dateTime = e.getPaymentDate();
			return dateTime.getYear() == year && dateTime.get(WeekFields.ISO.weekOfYear()) == week;
		}).collect(Collectors.toList());
		return processPayments(records);
	}

	public Map<String, Object> getMonthlyPayments(int year, int month) {
		List<Payments> records = paymentsRepository.findAll().stream()
				.filter(e -> e.getPaymentDate().getYear() == year && e.getPaymentDate().getMonthValue() == month)
				.collect(Collectors.toList());
		return processPayments(records);
	}

	public Map<String, Object> getYearlyPayments(int year) {
		List<Payments> records = paymentsRepository.findAll().stream().filter(e -> e.getPaymentDate().getYear() == year)
				.collect(Collectors.toList());
		return processPayments(records);
	}

	public Map<String, Object> getPaymentsByDateRange(LocalDate start, LocalDate end) {
		LocalDateTime startDateTime = start.atStartOfDay();
		LocalDateTime endDateTime = end.plusDays(1).atStartOfDay();
		List<Payments> records = paymentsRepository.findByPaymentDateBetween(startDateTime, endDateTime);
		return processPayments(records);
	}

}

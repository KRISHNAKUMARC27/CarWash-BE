package com.sas.carwash.service;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.sas.carwash.entity.Estimate;
import com.sas.carwash.entity.JobCard;
import com.sas.carwash.entity.JobSpares;
import com.sas.carwash.entity.Payments;
import com.sas.carwash.entity.ServicePackage;
import com.sas.carwash.model.CreditPayment;
import com.sas.carwash.model.MultiCreditPayment;
import com.sas.carwash.model.PaymentSplit;
import com.sas.carwash.repository.EstimateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EstimateService {

	private final EstimateRepository estimateRepository;
	private final UtilService utilService;
	private final SpringTemplateEngine templateEngine;
	private final PaymentsService paymentsService;
	private final ServicePackageService servicePackageService;

	public List<?> findAll() throws Exception {
		// estimateData();
		return estimateRepository.findAllByOrderByIdDesc();
	}

	public Estimate findById(String id) throws Exception {
		return estimateRepository.findById(id).orElseThrow(() -> new RuntimeException("Estimate ID not found " + id));
	}

	public List<Estimate> findByCreditFlag() {
		return estimateRepository.findByCreditFlagAndCreditSettledFlagOrderByIdDesc(true, false);
	}

	public Estimate save(Estimate estimate) throws Exception {
		JobCard jobCard = utilService.findById(estimate.getJobObjId());
		if (jobCard == null) {
			throw new Exception("JobCard cannot be null");
		}
		if (jobCard.getInvoiceObjId() != null) {
			throw new Exception("Bill already generated");
		}
		JobSpares jobSpares = utilService.findByIdJobSpares(estimate.getJobObjId());
		if (jobSpares == null) {
			throw new Exception("JobSpares cannot be null");
		}
		if (estimate.getId() == null) {
			estimate.setEstimateId(utilService.getNextSequenceForNewSequence("estimateId"));
			estimate.setBillCloseDate(LocalDateTime.now());
		}
		for (PaymentSplit split : estimate.getPaymentSplitList()) {

			if ("DELETE".equals(split.getFlag()) && "PACKAGE".equals(split.getPaymentMode())) {
				throw new IllegalArgumentException("Cannot delete PACKAGE payment splits.");
			}

			if (split.getPaymentDate() == null)
				split.setPaymentDate(LocalDateTime.now());

			if (!"CREDIT".equals(split.getPaymentMode()) && !"PACKAGE".equals(split.getPaymentMode())) {
				if ("ADD".equals(split.getFlag())) {
					Payments payments = Payments.builder()
							.paymentAmount(split.getPaymentAmount())
							.paymentDate(split.getPaymentDate())
							.paymentMode(split.getPaymentMode())
							.category("ESTIMATE")
							.categoryFieldId(estimate.getEstimateId())
							.isCreditPayment(false)
							.build();
					payments = paymentsService.save(payments);
					split.setPaymentId(payments.getId());
				} else if ("MODIFY".equals(split.getFlag())) {
					Payments payments = paymentsService.findById(split.getPaymentId());

					// record modified payments.
					payments = paymentsService.recordModifiedPayments(split.getPaymentAmount(), payments);

					payments.setPaymentAmount(split.getPaymentAmount());
					payments.setPaymentMode(split.getPaymentMode());
					paymentsService.save(payments);
				} else if ("DELETE".equals(split.getFlag())) {
					paymentsService.deleteById(split.getPaymentId());
				}
			}

		}

		for (CreditPayment credit : estimate.getCreditPaymentList()) {

			if (credit.getCreditDate() == null)
				credit.setCreditDate(LocalDateTime.now());

			if ("ADD".equals(credit.getFlag())) {
				Payments payments = Payments.builder()
						.paymentAmount(credit.getAmount())
						.paymentDate(credit.getCreditDate())
						.paymentMode(credit.getPaymentMode())
						.category("ESTIMATE")
						.categoryFieldId(estimate.getEstimateId())
						.isCreditPayment(true)
						.build();
				payments = paymentsService.save(payments);
				credit.setPaymentId(payments.getId());
			} else if ("MODIFY".equals(credit.getFlag())) {
				Payments payments = paymentsService.findById(credit.getPaymentId());

				// record modified payments.
				payments = paymentsService.recordModifiedPayments(credit.getAmount(), payments);

				payments.setPaymentAmount(credit.getAmount());
				payments.setPaymentMode(credit.getPaymentMode());
				paymentsService.save(payments);
			} else if ("DELETE".equals(credit.getFlag())) {
				paymentsService.deleteById(credit.getPaymentId());
			}
		}

		// **Filter out the paymentssplit marked for DELETE** before saving
		List<PaymentSplit> filteredPaymentSplitList = estimate.getPaymentSplitList().stream()
				.filter(split -> !"DELETE".equals(split.getFlag())).collect(Collectors.toList());

		// Update the paymentssplit list and set action to null
		filteredPaymentSplitList.forEach(split -> split.setFlag(null));
		estimate.setPaymentSplitList(filteredPaymentSplitList);

		// **Filter out the creditsplit marked for DELETE** before saving
		List<CreditPayment> filteredCreditPaymentList = estimate.getCreditPaymentList().stream()
				.filter(split -> !"DELETE".equals(split.getFlag())).collect(Collectors.toList());

		// Update the creditsplit list and set action to null
		filteredCreditPaymentList.forEach(split -> split.setFlag(null));
		estimate.setCreditPaymentList(filteredCreditPaymentList);

		estimate = utilService.updatePaymentListForCreditEstimate(estimate, estimate.getPendingAmount());

		estimate = estimateRepository.save(estimate);
		jobCard.setEstimateObjId(estimate.getId());
		utilService.simpleSave(jobCard);

		jobSpares.setEstimateObjId(estimate.getId());
		finalizePackageDeductionForEstimate(estimate, jobSpares);
		utilService.simpleSaveJobSpares(jobSpares);
		
		return estimate;
	}

		public void finalizePackageDeductionForEstimate(Estimate estimate, JobSpares jobSpares) throws Exception {
		BigDecimal newDeducted = utilService.extractPackageDeductionAmount(estimate.getPaymentSplitList());
		String packageId = utilService.extractPackageDeductionPackageId(estimate.getPaymentSplitList());
		BigDecimal previousDeducted = Optional.ofNullable(jobSpares.getPackageDeductedAmount()).orElse(BigDecimal.ZERO);

		if (newDeducted.compareTo(BigDecimal.ZERO) <= 0 && previousDeducted.compareTo(BigDecimal.ZERO) <= 0) {
			return; // Nothing to do
		}

		if (packageId == null || packageId.isBlank()) {
			if (newDeducted.compareTo(BigDecimal.ZERO) > 0) {
				throw new IllegalStateException("Missing service package ID for PACKAGE payment.");
			}
			return; // No package and nothing to refund
		}

		ServicePackage servicePackage = servicePackageService.findById(packageId);
		if (servicePackage == null) {
			if (newDeducted.compareTo(BigDecimal.ZERO) > 0) {
				throw new IllegalStateException("No open service package found, but PACKAGE payment exists.");
			}
			return; // No package and nothing to refund
		}

		// Refund previously deducted amount
		BigDecimal updatedAmount = servicePackage.getAmount().add(previousDeducted);

		// Check and subtract new deduction
		if (newDeducted.compareTo(updatedAmount) > 0) {
			throw new IllegalStateException("Package deduction exceeds available package balance.");
		}

		updatedAmount = updatedAmount.subtract(newDeducted);
		servicePackage.setAmount(updatedAmount);
		
		Map<Integer, BigDecimal> jobMap = Optional.ofNullable(servicePackage.getJobIdToDeductedAmount())
				.orElse(new HashMap<>());

		int jobId = jobSpares.getJobId(); // assuming this exists
		BigDecimal existing = jobMap.getOrDefault(jobId, BigDecimal.ZERO);

		if (existing.compareTo(newDeducted) != 0) {
			jobMap.put(jobId, newDeducted);
			servicePackage.setJobIdToDeductedAmount(jobMap);
			servicePackageService.update(servicePackage);
		}

		// Reflect new deduction in jobSpares
		jobSpares.setPackageDeductedAmount(newDeducted);
	}

	public Estimate findByJobObjId(String id) {
		return estimateRepository.findByJobObjId(id);
	}

	// public Estimate saveFastEstimate(JobCard jobCard, JobSpares jobSpares, FastJobCardRecord fastJobCard) throws Exception {
	// 	Estimate estimate = Estimate.builder()
	// 			.jobId(jobCard.getJobId())
	// 			.ownerName(jobCard.getOwnerName())
	// 			.ownerPhoneNumber(jobCard.getOwnerPhoneNumber())
	// 			.vehicleRegNo(jobCard.getVehicleRegNo())
	// 			.vehicleName(jobCard.getVehicleName())
	// 			.grandTotal(jobSpares.getGrandTotal())
	// 			.jobObjId(jobSpares.getId())
	// 			.paymentSplitList(List.of(PaymentSplit.builder()
	// 					.paymentAmount(jobSpares.getGrandTotal())
	// 					.paymentMode(fastJobCard.paymentMode())
	// 					.flag("ADD")
	// 					.build()))
	// 			.creditPaymentList(new ArrayList<>())
	// 			.pendingAmount(BigDecimal.ZERO)
	// 			.creditFlag(false)
	// 			.creditSettledFlag(false)
	// 			.build();

	// 	return save(estimate);
	// }

	public Map<String, String> multiCreditSettlement(MultiCreditPayment multiCreditPayment) throws Exception {
		Map<String, String> response = new HashMap<>();
		List<Estimate> estimateList = new ArrayList<>();
		for (String id : multiCreditPayment.getEstimateIds()) {
			Estimate estimate = findById(id);
			estimateList.add(estimate);
		}

		// Sort in ascending order of pending amount because lets try to close small
		// fries earlier.
		estimateList.sort(Comparator.comparing(Estimate::getPendingAmount));

		List<Estimate> deepCopiedEstimateList = estimateList.stream().map(Estimate::new).collect(Collectors.toList());

		Map<Estimate, BigDecimal> settleMap = calculateNecessaryCreditSettlementPerEstimate(estimateList,
				multiCreditPayment);
		try {

			settleMap.forEach((estimate, pendingAmount) -> {

				List<CreditPayment> creditPaymentList = estimate.getCreditPaymentList();
				CreditPayment creditPayment = CreditPayment.builder().amount(pendingAmount)
						.paymentMode(multiCreditPayment.getPaymentMode()).comment(multiCreditPayment.getComment())
						.creditDate(LocalDateTime.now()).build();
				Payments payments = Payments.builder()
						.paymentAmount(creditPayment.getAmount())
						.paymentDate(creditPayment.getCreditDate())
						.paymentMode(creditPayment.getPaymentMode())
						.category("ESTIMATE")
						.categoryFieldId(estimate.getEstimateId())
						.isCreditPayment(true)
						.build();
				payments = paymentsService.save(payments);
				creditPayment.setPaymentId(payments.getId());

				creditPaymentList.add(creditPayment);

				BigDecimal grandTotal = estimate.getGrandTotal();

				BigDecimal totalPaidExcludingCredit = estimate.getPaymentSplitList().stream()
						.filter(split -> !"CREDIT".equals(split.getPaymentMode())) // Exclude CREDIT payments
						.map(split -> split.getPaymentAmount() != null ? split.getPaymentAmount() : BigDecimal.ZERO) // Handle
																														// null
						// values
						.reduce(BigDecimal.ZERO, BigDecimal::add); // Sum up payment amounts

				BigDecimal totalCreditPayments = estimate.getCreditPaymentList().stream()
						.map(credit -> credit.getAmount() != null ? credit.getAmount() : BigDecimal.ZERO)
						.reduce(BigDecimal.ZERO, BigDecimal::add);

				BigDecimal newPendingAmount = grandTotal.subtract(totalPaidExcludingCredit)
						.subtract(totalCreditPayments);
				if (newPendingAmount.compareTo(BigDecimal.ZERO) > 0) {
					estimate.setPendingAmount(newPendingAmount);
				} else {
					estimate.setPendingAmount(BigDecimal.ZERO);
					estimate.setCreditSettledFlag(true);
				}

				estimateRepository.save(estimate);
			});

			if (multiCreditPayment.getAmount().compareTo(BigDecimal.ZERO) > 0) {
				response.put("result", "You have paid extra amount of " + multiCreditPayment.getAmount());

			} else if (multiCreditPayment.getAmount().compareTo(BigDecimal.ZERO) < 0) {
				response.put("result",
						"Insufficient amount paid for selected estimates" + multiCreditPayment.getAmount());
			} else {
				if (estimateList.size() == settleMap.size()) {
					response.put("result", "Settlement amount tallied");
				} else {
					response.put("result", "Insufficient amount paid ");
				}
			}
		} catch (Exception ex) {
			estimateRepository.saveAll(deepCopiedEstimateList);
			// deepCopiedEstimateList.stream().forEach(estimate ->
			// estimateRepository.save(estimate));
			throw new Exception("Rolled back changes. Error: " + ex.getMessage());
		}
		return response;
	}

	// This method is used to create a map of Estimates with value as the
	// creditPayment amount.
	// User could have paid less amount so use it on the lists 1 by 1.
	// If the settleamount is less than or equal to first estimate then break there
	// itself.
	// else keep subtracting the settleamount and update in the object itself. also
	// fill the map.
	private Map<Estimate, BigDecimal> calculateNecessaryCreditSettlementPerEstimate(List<Estimate> estimateList,
			MultiCreditPayment multiCreditPayment) {

		Map<Estimate, BigDecimal> settleMap = new HashMap<>();

		for (Estimate estimate : estimateList) {
			BigDecimal estimatePendingAmount = estimate.getPendingAmount();
			if (multiCreditPayment.getAmount().compareTo(estimatePendingAmount) >= 0) {
				settleMap.put(estimate, estimatePendingAmount);
				multiCreditPayment.setAmount(multiCreditPayment.getAmount().subtract(estimatePendingAmount));
			} else {
				settleMap.put(estimate, multiCreditPayment.getAmount());
				multiCreditPayment.setAmount(BigDecimal.ZERO);
				break;
			}

		}

		return settleMap;

	}

	public void generateEstimatePdf(Map<String, Object> data, String outputPath) throws Exception {
		// Render HTML with dynamic data
		Context context = new Context();
		context.setVariables(data);
		String htmlContent = templateEngine.process("estimate", context);

		// Convert HTML to PDF
		try (OutputStream os = new FileOutputStream(outputPath)) {
			ITextRenderer renderer = new ITextRenderer();
			renderer.setDocumentFromString(htmlContent);
			renderer.layout();
			renderer.createPDF(os);
		}
	}

	// REPORT START
	private Map<String, Object> processEstimate(List<Estimate> records) {
		Map<String, Object> result = new HashMap<>();

		// grandTotal amount
		BigDecimal total = records.stream()
				.map(exp -> Optional.ofNullable(exp.getGrandTotal()).orElse(BigDecimal.ZERO))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		result.put("total", total);

		// Group by credit or not
		Map<Boolean, Long> byType = records.stream()
				.collect(Collectors.groupingBy(exp -> Optional.ofNullable(exp.getCreditFlag()).orElse(false),
						Collectors.counting()));
		result.put("byCredit", byType);

		// pending amount
		BigDecimal pending = records.stream()
				.map(exp -> Optional.ofNullable(exp.getPendingAmount()).orElse(BigDecimal.ZERO))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		result.put("pending", pending);

		return result;
	}

	public Map<String, Object> getDailyEstimate(LocalDate date) {
		LocalDateTime start = date.atStartOfDay();
		LocalDateTime end = start.plusDays(1);
		List<Estimate> records = estimateRepository.findByBillCloseDateBetween(start, end);
		return processEstimate(records);
	}

	public Map<String, Object> getWeeklyEstimate(int year, int week) {
		List<Estimate> records = estimateRepository.findAll().stream().filter(e -> {
			LocalDateTime dateTime = e.getBillCloseDate();
			return dateTime.getYear() == year && dateTime.get(WeekFields.ISO.weekOfYear()) == week;
		}).collect(Collectors.toList());
		return processEstimate(records);
	}

	public Map<String, Object> getMonthlyEstimate(int year, int month) {
		List<Estimate> records = estimateRepository.findAll().stream()
				.filter(e -> e.getBillCloseDate().getYear() == year && e.getBillCloseDate().getMonthValue() == month)
				.collect(Collectors.toList());
		return processEstimate(records);
	}

	public Map<String, Object> getYearlyEstimate(int year) {
		List<Estimate> records = estimateRepository.findAll().stream()
				.filter(e -> e.getBillCloseDate().getYear() == year)
				.collect(Collectors.toList());
		return processEstimate(records);
	}

	public Map<String, Object> getEstimateByDateRange(LocalDate start, LocalDate end) {
		LocalDateTime startDateTime = start.atStartOfDay();
		LocalDateTime endDateTime = end.plusDays(1).atStartOfDay();
		List<Estimate> records = estimateRepository.findByBillCloseDateBetween(startDateTime, endDateTime);
		return processEstimate(records);
	}

	// REPORT END
}

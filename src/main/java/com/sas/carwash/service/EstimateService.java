package com.sas.carwash.service;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
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
	private final JobCardService jobCardService;
	private final SpringTemplateEngine templateEngine;

	public List<?> findAll() throws Exception {
		// estimateData();
		return estimateRepository.findAllByOrderByIdDesc();
	}

	public Estimate findById(String id) throws Exception {
		return estimateRepository.findById(id).orElseThrow(() -> new RuntimeException("Estimate ID not found"));
	}

	public List<Estimate> findByCreditFlag() {
		return estimateRepository.findByCreditFlagAndCreditSettledFlagOrderByIdDesc(true, false);
	}

	public Estimate save(Estimate estimate) throws Exception {
		JobCard jobCard = jobCardService.findById(estimate.getJobObjId());
		if (jobCard == null) {
			throw new Exception("JobCard cannot be null");
		}
		if (jobCard.getInvoiceObjId() != null) {
			throw new Exception("Bill already generated");
		}
		JobSpares jobSpares = jobCardService.findByIdJobSpares(estimate.getJobObjId());
		if (jobSpares == null) {
			throw new Exception("JobSpares cannot be null");
		}
		if (estimate.getId() == null) {
			estimate.setEstimateId(jobCardService.getNextSequenceForNewSequence("estimateId"));
			estimate.setBillCloseDate(LocalDateTime.now());
		}
		estimate.getCreditPaymentList().stream().forEach(credit -> {
			if (credit.getCreditDate() == null)
				credit.setCreditDate(LocalDateTime.now());
		});
		estimate = estimateRepository.save(estimate);
		jobCard.setEstimateObjId(estimate.getId());
		jobCardService.simpleSave(jobCard);
		jobSpares.setEstimateObjId(estimate.getId());
		jobCardService.simpleSaveJobSpares(jobSpares);
		return estimate;
	}

	public Estimate findByJobObjId(String id) {
		return estimateRepository.findByJobObjId(id);
	}

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
			if (estimatePendingAmount.compareTo(multiCreditPayment.getAmount()) >= 0) {
				settleMap.put(estimate, multiCreditPayment.getAmount());
				BigDecimal remainderAmount = multiCreditPayment.getAmount().subtract(estimatePendingAmount);
				multiCreditPayment.setAmount(remainderAmount);
				break;
			} else {
				settleMap.put(estimate, estimatePendingAmount);
				BigDecimal remainderAmount = multiCreditPayment.getAmount().subtract(estimatePendingAmount);
				multiCreditPayment.setAmount(remainderAmount);
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

}

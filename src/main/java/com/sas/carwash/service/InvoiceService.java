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

import com.sas.carwash.entity.Invoice;
import com.sas.carwash.entity.JobCard;
import com.sas.carwash.entity.JobSpares;
import com.sas.carwash.entity.Payments;
import com.sas.carwash.model.CreditPayment;
import com.sas.carwash.model.MultiCreditPayment;
import com.sas.carwash.model.PaymentSplit;
import com.sas.carwash.repository.InvoiceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

	private final InvoiceRepository invoiceRepository;
	private final UtilService utilService;
    private final SpringTemplateEngine templateEngine;
	private final PaymentsService paymentsService;
    
	public List<?> findAll() throws Exception {
		//invoiceData();
		return invoiceRepository.findAllByOrderByIdDesc();
	}

	public Invoice findById(String id) throws Exception {
		return invoiceRepository.findById(id).orElseThrow(() -> new RuntimeException("Invoice ID not found " + id));
	}

	public List<Invoice> findByCreditFlag() {
		return invoiceRepository.findByCreditFlagAndCreditSettledFlagOrderByIdDesc(true, false);
	}

	public Invoice save(Invoice invoice) throws Exception {
		JobCard jobCard = utilService.findById(invoice.getJobObjId());
		if (jobCard == null) {
			throw new Exception("JobCard cannot be null");
		}
		if (jobCard.getEstimateObjId() != null) {
			throw new Exception("Bill already generated");
		}
		JobSpares jobSpares = utilService.findByIdJobSpares(invoice.getJobObjId());
		if (jobSpares == null) {
			throw new Exception("JobSpares cannot be null");
		}
		if (invoice.getId() == null) {
			invoice.setInvoiceId(utilService.getNextSequenceForNewSequence("invoiceId"));
			invoice.setBillCloseDate(LocalDateTime.now());
		}

		for (PaymentSplit split : invoice.getPaymentSplitList()) {

			if (split.getPaymentDate() == null)
				split.setPaymentDate(LocalDateTime.now());

			if (!"CREDIT".equals(split.getPaymentMode())) {
				if ("ADD".equals(split.getFlag())) {
					Payments payments = Payments.builder()
							.paymentAmount(split.getPaymentAmount())
							.paymentDate(split.getPaymentDate())
							.paymentMode(split.getPaymentMode())
							.category("INVOICE")
							.categoryFieldId(invoice.getInvoiceId())
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

		for (CreditPayment credit : invoice.getCreditPaymentList()) {

			if (credit.getCreditDate() == null)
				credit.setCreditDate(LocalDateTime.now());

			if ("ADD".equals(credit.getFlag())) {
				Payments payments = Payments.builder()
						.paymentAmount(credit.getAmount())
						.paymentDate(credit.getCreditDate())
						.paymentMode(credit.getPaymentMode())
						.category("INVOICE")
						.categoryFieldId(invoice.getInvoiceId())
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
		List<PaymentSplit> filteredPaymentSplitList = invoice.getPaymentSplitList().stream()
				.filter(split -> !"DELETE".equals(split.getFlag())).collect(Collectors.toList());

		// Update the paymentssplit list and set action to null
		filteredPaymentSplitList.forEach(split -> split.setFlag(null));
		invoice.setPaymentSplitList(filteredPaymentSplitList);

		// **Filter out the creditsplit marked for DELETE** before saving
		List<CreditPayment> filteredCreditPaymentList = invoice.getCreditPaymentList().stream()
				.filter(split -> !"DELETE".equals(split.getFlag())).collect(Collectors.toList());

		// Update the creditsplit list and set action to null
		filteredCreditPaymentList.forEach(split -> split.setFlag(null));
		invoice.setCreditPaymentList(filteredCreditPaymentList);

		invoice = utilService.updatePaymentListForCreditInvoice(invoice, invoice.getPendingAmount());

		invoice = invoiceRepository.save(invoice);
		jobCard.setInvoiceObjId(invoice.getId());
		utilService.simpleSave(jobCard);
		jobSpares.setInvoiceObjId(invoice.getId());
		utilService.simpleSaveJobSpares(jobSpares);
		return invoice;
	}

	public Invoice findByJobObjId(String id) {
		return invoiceRepository.findByJobObjId(id);
	}

	// public Invoice saveFastInvoice(JobCard jobCard, JobSpares jobSpares, FastJobCardRecord fastJobCard) throws Exception {
	// 	Invoice invoice = Invoice.builder()
	// 			.jobId(jobCard.getJobId())
	// 			.ownerName(jobCard.getOwnerName())
	// 			.ownerPhoneNumber(jobCard.getOwnerPhoneNumber())
	// 			.vehicleRegNo(jobCard.getVehicleRegNo())
	// 			.vehicleName(jobCard.getVehicleName())
	// 			.grandTotal(jobSpares.getGrandTotalWithGST())
	// 			.jobObjId(jobSpares.getId())
	// 			.paymentSplitList(List.of(PaymentSplit.builder()
	// 					.paymentAmount(jobSpares.getGrandTotalWithGST())
	// 					.paymentMode(fastJobCard.paymentMode())
	// 					.flag("ADD")
	// 					.build()))
	// 			.creditPaymentList(new ArrayList<>())
	// 			.pendingAmount(BigDecimal.ZERO)
	// 			.creditFlag(false)
	// 			.creditSettledFlag(false)
	// 			.build();

	// 	return save(invoice);
	// }

	public Map<String, String> multiCreditSettlement(MultiCreditPayment multiCreditPayment) throws Exception {
		Map<String, String> response = new HashMap<>();
		List<Invoice> invoiceList = new ArrayList<>();
		for (String id : multiCreditPayment.getInvoiceIds()) {
			Invoice invoice = findById(id);
			invoiceList.add(invoice);
		}
		
		//Sort in ascending order of pending amount because lets try to close small fries earlier.
		invoiceList.sort(Comparator.comparing(Invoice::getPendingAmount));

		List<Invoice> deepCopiedInvoiceList = invoiceList.stream().map(Invoice::new).collect(Collectors.toList());

		Map<Invoice, BigDecimal> settleMap = calculateNecessaryCreditSettlementPerInvoice(invoiceList,
				multiCreditPayment);
		try {

			settleMap.forEach((invoice, pendingAmount) -> {

				List<CreditPayment> creditPaymentList = invoice.getCreditPaymentList();
				CreditPayment creditPayment = CreditPayment.builder().amount(pendingAmount)
						.paymentMode(multiCreditPayment.getPaymentMode()).comment(multiCreditPayment.getComment())
						.creditDate(LocalDateTime.now()).build();
				Payments payments = Payments.builder()
						.paymentAmount(creditPayment.getAmount())
						.paymentDate(creditPayment.getCreditDate())
						.paymentMode(creditPayment.getPaymentMode())
						.category("INVOICE")
						.categoryFieldId(invoice.getInvoiceId())
						.isCreditPayment(true)
						.build();
				payments = paymentsService.save(payments);
				creditPayment.setPaymentId(payments.getId());

				creditPaymentList.add(creditPayment);
				
				BigDecimal grandTotal = invoice.getGrandTotal();

				BigDecimal totalPaidExcludingCredit = invoice.getPaymentSplitList().stream()
						.filter(split -> !"CREDIT".equals(split.getPaymentMode())) // Exclude CREDIT payments
						.map(split -> split.getPaymentAmount() != null ? split.getPaymentAmount() : BigDecimal.ZERO) // Handle
																														// null
						// values
						.reduce(BigDecimal.ZERO, BigDecimal::add); // Sum up payment amounts

				BigDecimal totalCreditPayments = invoice.getCreditPaymentList().stream()
						.map(credit -> credit.getAmount() != null ? credit.getAmount() : BigDecimal.ZERO)
						.reduce(BigDecimal.ZERO, BigDecimal::add);

				BigDecimal newPendingAmount = grandTotal.subtract(totalPaidExcludingCredit)
						.subtract(totalCreditPayments);
				if (newPendingAmount.compareTo(BigDecimal.ZERO) > 0) {
					invoice.setPendingAmount(newPendingAmount);
				} else {
					invoice.setPendingAmount(BigDecimal.ZERO);
					invoice.setCreditSettledFlag(true);
				}

				invoiceRepository.save(invoice);
			});

			if (multiCreditPayment.getAmount().compareTo(BigDecimal.ZERO) > 0) {
				response.put("result", "You have paid extra amount of " + multiCreditPayment.getAmount());

			} else if (multiCreditPayment.getAmount().compareTo(BigDecimal.ZERO) < 0) {
				response.put("result",
						"Insufficient amount paid for selected invoices" + multiCreditPayment.getAmount());
			} else {
				if (invoiceList.size() == settleMap.size()) {
					response.put("result", "Settlement amount tallied");
				} else {
					response.put("result", "Insufficient amount paid ");
				}
			}
		} catch (Exception ex) {
			invoiceRepository.saveAll(deepCopiedInvoiceList);
			//deepCopiedInvoiceList.stream().forEach(invoice -> invoiceRepository.save(invoice));
			throw new Exception("Rolled back changes. Error: " + ex.getMessage());
		}
		return response;
	}

	// This method is used to create a map of Invoices with value as the creditPayment amount.
	// User could have paid less amount so use it on the lists 1 by 1. 
	// If the settleamount is less than or equal to first invoice then break there itself.
	// else keep subtracting the settleamount and update in the object itself. also fill the map.
	private Map<Invoice, BigDecimal> calculateNecessaryCreditSettlementPerInvoice(List<Invoice> invoiceList,
			MultiCreditPayment multiCreditPayment) {

		Map<Invoice, BigDecimal> settleMap = new HashMap<>();

		for (Invoice invoice : invoiceList) {
			BigDecimal invoicePendingAmount = invoice.getPendingAmount();
			if (multiCreditPayment.getAmount().compareTo(invoicePendingAmount) >= 0) {
				settleMap.put(invoice, invoicePendingAmount);
				multiCreditPayment.setAmount(multiCreditPayment.getAmount().subtract(invoicePendingAmount));
			} else {
				settleMap.put(invoice, multiCreditPayment.getAmount());
				multiCreditPayment.setAmount(BigDecimal.ZERO);
				break;
			}

		}

		return settleMap;

	}
	
	
	public void generateInvoicePdf(Map<String, Object> data, String outputPath) throws Exception {
        // Render HTML with dynamic data
        Context context = new Context();
        context.setVariables(data);
        String htmlContent = templateEngine.process("invoice", context);

        // Convert HTML to PDF
        try (OutputStream os = new FileOutputStream(outputPath)) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(os);
        }
    }
	
	// REPORT START
	private Map<String, Object> processInvoice(List<Invoice> records) {
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

	public Map<String, Object> getDailyInvoice(LocalDate date) {
		LocalDateTime start = date.atStartOfDay();
		LocalDateTime end = start.plusDays(1);
		List<Invoice> records = invoiceRepository.findByBillCloseDateBetween(start, end);
		return processInvoice(records);
	}

	public Map<String, Object> getWeeklyInvoice(int year, int week) {
		List<Invoice> records = invoiceRepository.findAll().stream().filter(e -> {
			LocalDateTime dateTime = e.getBillCloseDate();
			return dateTime.getYear() == year && dateTime.get(WeekFields.ISO.weekOfYear()) == week;
		}).collect(Collectors.toList());
		return processInvoice(records);
	}

	public Map<String, Object> getMonthlyInvoice(int year, int month) {
		List<Invoice> records = invoiceRepository.findAll().stream()
				.filter(e -> e.getBillCloseDate().getYear() == year && e.getBillCloseDate().getMonthValue() == month)
				.collect(Collectors.toList());
		return processInvoice(records);
	}

	public Map<String, Object> getYearlyInvoice(int year) {
		List<Invoice> records = invoiceRepository.findAll().stream().filter(e -> e.getBillCloseDate().getYear() == year)
				.collect(Collectors.toList());
		return processInvoice(records);
	}

	public Map<String, Object> getInvoiceByDateRange(LocalDate start, LocalDate end) {
		LocalDateTime startDateTime = start.atStartOfDay();
		LocalDateTime endDateTime = end.plusDays(1).atStartOfDay();
		List<Invoice> records = invoiceRepository.findByBillCloseDateBetween(startDateTime, endDateTime);
		return processInvoice(records);
	}

	// REPORT END

}

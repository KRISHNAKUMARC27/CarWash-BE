package com.sas.carwash.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sas.carwash.entity.Invoice;
import com.sas.carwash.entity.JobCard;
import com.sas.carwash.entity.JobSpares;
import com.sas.carwash.model.CreditPayment;
import com.sas.carwash.model.MultiCreditPayment;
import com.sas.carwash.repository.InvoiceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

	private final InvoiceRepository invoiceRepository;
	private final JobCardService jobCardService;

	public List<?> findAll() {
		return invoiceRepository.findAllByOrderByIdDesc();
	}

	public Invoice findById(String id) throws Exception {
		return invoiceRepository.findById(id).orElseThrow(() -> new RuntimeException("Invoice ID not found"));
	}

	public List<Invoice> findByCreditFlag() {
		return invoiceRepository.findByCreditFlagAndCreditSettledFlagOrderByIdDesc(true, false);
	}

	public Invoice save(Invoice invoice) throws Exception {
		JobCard jobCard = jobCardService.findById(invoice.getJobObjId());
		if (jobCard == null) {
			throw new Exception("JobCard cannot be null");
		}
		JobSpares jobSpares = jobCardService.findByIdJobSpares(invoice.getJobObjId());
		if (jobSpares == null) {
			throw new Exception("JobSpares cannot be null");
		}
		if (invoice.getId() == null) {
			invoice.setInvoiceId(jobCardService.getNextSequenceForNewSequence("invoiceId"));
			invoice.setBillCloseDate(LocalDateTime.now());
		}
		invoice.getCreditPaymentList().stream().forEach(credit -> {
			if (credit.getCreditDate() == null)
				credit.setCreditDate(LocalDateTime.now());
		});
		invoice = invoiceRepository.save(invoice);
		jobCard.setInvoiceObjId(invoice.getId());
		jobCardService.simpleSave(jobCard);
		jobSpares.setInvoiceObjId(invoice.getId());
		jobCardService.simpleSaveJobSpares(jobSpares);
		return invoice;
	}

	public Invoice findByJobObjId(String id) {
		return invoiceRepository.findByJobObjId(id);
	}

	public Map<String, String> multiCreditSettlement(MultiCreditPayment multiCreditPayment) throws Exception {
		Map<String, String> response = new HashMap<>();
		List<Invoice> invoiceList = new ArrayList<>();
		for (String id : multiCreditPayment.getInvoiceIds()) {
			Invoice invoice = findById(id);
			invoiceList.add(invoice);
		}
		
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
			if (invoicePendingAmount.compareTo(multiCreditPayment.getAmount()) >= 0) {
				settleMap.put(invoice, multiCreditPayment.getAmount());
				BigDecimal remainderAmount = multiCreditPayment.getAmount().subtract(invoicePendingAmount);
				multiCreditPayment.setAmount(remainderAmount);
				break;
			} else {
				settleMap.put(invoice, invoicePendingAmount);
				BigDecimal remainderAmount = multiCreditPayment.getAmount().subtract(invoicePendingAmount);
				multiCreditPayment.setAmount(remainderAmount);
			}
		}

		return settleMap;

	}
}

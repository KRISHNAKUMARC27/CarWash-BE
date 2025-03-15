package com.sas.carwash.service;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

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
    private final SpringTemplateEngine templateEngine;
    
	public List<?> findAll() throws Exception {
		//invoiceData();
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
		if (jobCard.getEstimateObjId() != null) {
			throw new Exception("Bill already generated");
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
	
//	public void invoiceData() throws Exception {
//		Map<String, Object> data = new HashMap<>();
//		data.put("vehNo", "TN56C7799");
//		data.put("ownerName", "M/s VPR MAHAL, METTUKADAI, ERODE");
//		data.put("kms", "VPR MAHAL, METTUKADAI, ERODE");
//		data.put("invoiceNo", "387");
//		data.put("date", "08-01-2025");
//		data.put("mode", "Credit");
//		data.put("nextFreeCheckKms", "");
//		data.put("vehicleModel", "");
//		data.put("nextServiceKms", "");
//		
//		data.put("products", List.of(
//		    Map.of("name", "Gyproc Board - Saint Gobain", "hsnCode", "68091100", "qty", 110, "rate", 360.17, "gst", "18%", "amount", 39618.65),
//		    Map.of("name", "Xpert Ceiling Angle", "hsnCode", "72169910", "qty", 60, "rate", 58.47, "gst", "18%", "amount", 3508.48)
//		));
//		data.put("taxDetails", List.of(
//		    Map.of("taxableValue", "47345.52", "cgstPercent", "9.0", "cgstAmt", "4261.88", "sgstPercent", "9.0", "sgstAmt", "4261.88", "netPercent", "18", "amount", "8523.77")
//		));
//		data.put("totalTaxAmt", "8588.53");
//		data.put("roundOff", "450.00");
//		data.put("netAmount", "57688.00");
//		data.put("amountInWords", "Fifty Seven Thousand Six Hundred Eighty Eight Only");
//		
//		generateInvoicePdf(data,"invoice.pdf");
//	}
	

}

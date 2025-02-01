package com.sas.carwash.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sas.carwash.entity.Invoice;
import com.sas.carwash.entity.ReceiptInvoice;
import com.sas.carwash.model.MultiCreditPayment;
import com.sas.carwash.service.InvoiceService;
import com.sas.carwash.service.ReceiptInvoiceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/invoice")
@RequiredArgsConstructor
@Slf4j
public class InvoiceController {

	private final InvoiceService invoiceService;
	private final ReceiptInvoiceService receiptInvoiceService;

	@GetMapping
	public List<?> findAll() throws Exception {
		return invoiceService.findAll();
	}

	@GetMapping("/{id}")
	public Invoice findById(@PathVariable String id) throws Exception {
		return invoiceService.findById(id);
	}

	@PostMapping
	public Invoice save(@RequestBody Invoice invoice) throws Exception {
		return invoiceService.save(invoice);
	}

	@GetMapping("/jobObjId/{id}")
	public Invoice findByJobObjId(@PathVariable String id) {
		return invoiceService.findByJobObjId(id);
	}

	@GetMapping("/findByCreditFlag")
	public List<Invoice> findByCreditFlag() {
		return invoiceService.findByCreditFlag();
	}

	@PostMapping("/multiCreditSettlement")
	public Map<String, String> multiCreditSettlement(@RequestBody MultiCreditPayment multiCreditPayment)
			throws Exception {
		return invoiceService.multiCreditSettlement(multiCreditPayment);
	}

	@GetMapping("/receipt")
	public List<?> findAllReceipts() throws Exception {
		return receiptInvoiceService.findAll();
	}

	@GetMapping("/receipt/{id}")
	public ReceiptInvoice findByIdReceipts(@PathVariable String id) throws Exception {
		return receiptInvoiceService.findById(id);
	}

	@PostMapping("/receipt")
	public ReceiptInvoice saveReceipts(@RequestBody ReceiptInvoice invoice) throws Exception {
		return receiptInvoiceService.save(invoice);
	}

	@GetMapping("/receiptPdf/{id}")
	public ResponseEntity<?> generateReceiptPdf(@PathVariable String id) {

		try {
			return receiptInvoiceService.receiptPdf(id);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}
}

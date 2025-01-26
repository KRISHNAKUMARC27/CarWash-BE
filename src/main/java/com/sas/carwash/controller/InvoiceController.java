package com.sas.carwash.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sas.carwash.entity.Invoice;
import com.sas.carwash.model.MultiCreditPayment;
import com.sas.carwash.service.InvoiceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/invoice")
@RequiredArgsConstructor
@Slf4j
public class InvoiceController {

	private final InvoiceService invoiceService;

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
	public  Map<String, String> multiCreditSettlement(@RequestBody MultiCreditPayment multiCreditPayment) throws Exception {
		return invoiceService.multiCreditSettlement(multiCreditPayment);
	}

}

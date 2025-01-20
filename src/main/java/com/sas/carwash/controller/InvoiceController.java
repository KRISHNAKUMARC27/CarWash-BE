package com.sas.carwash.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sas.carwash.entity.Invoice;
import com.sas.carwash.entity.LaborCategory;
import com.sas.carwash.entity.LaborInventory;
import com.sas.carwash.model.LaborFilter;
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
	public List<?> findAll() {
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

}

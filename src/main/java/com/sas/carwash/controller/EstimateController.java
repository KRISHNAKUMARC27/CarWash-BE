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

import com.sas.carwash.entity.Estimate;
import com.sas.carwash.entity.ReceiptEstimate;
import com.sas.carwash.model.MultiCreditPayment;
import com.sas.carwash.service.EstimateService;
import com.sas.carwash.service.ReceiptEstimateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/estimate")
@RequiredArgsConstructor
@Slf4j
public class EstimateController {

	private final EstimateService estimateService;
	private final ReceiptEstimateService receiptEstimateService;

	@GetMapping
	public List<?> findAll() throws Exception {
		return estimateService.findAll();
	}

	@GetMapping("/{id}")
	public Estimate findById(@PathVariable String id) throws Exception {
		return estimateService.findById(id);
	}

	@PostMapping
	public ResponseEntity<?> save(@RequestBody Estimate estimate) throws Exception {
		try {
			return ResponseEntity.ok().body(estimateService.save(estimate));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/jobObjId/{id}")
	public Estimate findByJobObjId(@PathVariable String id) {
		return estimateService.findByJobObjId(id);
	}

	@GetMapping("/findByCreditFlag")
	public List<Estimate> findByCreditFlag() {
		return estimateService.findByCreditFlag();
	}

	@PostMapping("/multiCreditSettlement")
	public Map<String, String> multiCreditSettlement(@RequestBody MultiCreditPayment multiCreditPayment)
			throws Exception {
		return estimateService.multiCreditSettlement(multiCreditPayment);
	}

	@GetMapping("/receipt")
	public List<?> findAllReceipts() throws Exception {
		return receiptEstimateService.findAll();
	}

	@GetMapping("/receipt/{id}")
	public ReceiptEstimate findByIdReceipts(@PathVariable String id) throws Exception {
		return receiptEstimateService.findById(id);
	}

	@PostMapping("/receipt")
	public ReceiptEstimate saveReceipts(@RequestBody ReceiptEstimate estimate) throws Exception {
		return receiptEstimateService.save(estimate);
	}

	@GetMapping("/receiptPdf/{id}")
	public ResponseEntity<?> generateReceiptPdf(@PathVariable String id) {

		try {
			return receiptEstimateService.receiptPdf(id);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}
}

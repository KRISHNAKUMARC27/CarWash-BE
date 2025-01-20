package com.sas.carwash.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sas.carwash.entity.Invoice;
import com.sas.carwash.entity.JobCard;
import com.sas.carwash.entity.JobSpares;
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
		if(jobCard == null) {
			throw new Exception("JobCard cannot be null");
		}
		JobSpares jobSpares = jobCardService.findByIdJobSpares(invoice.getJobObjId());
		if(jobSpares == null) {
			throw new Exception("JobSpares cannot be null");
		}
		if (invoice.getId() == null) {  
			invoice.setInvoiceId(jobCardService.getNextSequenceForNewSequence("invoiceId"));
			invoice.setBillCloseDate(LocalDateTime.now());
		}
		invoice.getCreditPaymentList().stream().forEach(credit -> {
			if(credit.getCreditDate() == null)
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

}

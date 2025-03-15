package com.sas.carwash.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sas.carwash.entity.ReceiptInvoice;
import com.sas.carwash.repository.ReceiptInvoiceRepository;
import com.sas.carwash.utils.NumberToWordsConverter;
import com.sas.carwash.utils.PdfUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiptInvoiceService {

	private final ReceiptInvoiceRepository receiptRepository;
	private final JobCardService jobCardService;
	private final PdfUtils pdfUtils;

	public List<?> findAll() throws Exception {
		return receiptRepository.findAllByOrderByIdDesc();
	}

	public ReceiptInvoice findById(String id) throws Exception {
		return receiptRepository.findById(id).orElseThrow(() -> new RuntimeException("Receipt ID not found"));
	}

	public ReceiptInvoice save(ReceiptInvoice receipt) throws Exception {

		if (receipt.getId() == null) {
			receipt.setReceiptId(jobCardService.getNextSequenceForNewSequence("receiptId"));
			receipt.setReceiptDate(LocalDateTime.now());
		}

		return receiptRepository.save(receipt);
	}

	public ResponseEntity<ByteArrayResource> receiptPdf(String id) throws Exception {
		ReceiptInvoice receipt = findById(id);
		String paymentMode = receipt.getPaymentMode();

		String invoiceIdsString = receipt.getInvoiceIdList().stream().map(String::valueOf)
				.collect(Collectors.joining(", "));

		Map<String, Object> data = new HashMap<>();
		data.put("ownerName", receipt.getOwnerName());
		data.put("receiptNo", receipt.getReceiptId());
		data.put("date", receipt.getReceiptDate());
		data.put("mode", paymentMode);
		data.put("invoiceIds", invoiceIdsString);

		data.put("netAmount", receipt.getAmount());
		data.put("amountInWords", NumberToWordsConverter.convert(receipt.getAmount()));

		ByteArrayResource resource = pdfUtils.generateHTMLPdf(data,"receipt");

		String filename = "Bill_" + receipt.getReceiptId() + ".pdf";

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

		return ResponseEntity.ok().headers(headers).contentLength(resource.contentLength())
				.contentType(MediaType.APPLICATION_PDF).body(resource);
	}



}

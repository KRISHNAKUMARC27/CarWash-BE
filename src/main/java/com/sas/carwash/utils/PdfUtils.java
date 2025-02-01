package com.sas.carwash.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PdfUtils {
	
	private final SpringTemplateEngine templateEngine;
	
	public void generateInvoicePdfOnDisk(Map<String, Object> data, String outputPath) throws Exception {
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
	
	public ByteArrayResource generateHTMLPdf(Map<String, Object> data, String type) throws Exception {
		// Render HTML with dynamic data
		Context context = new Context();
		context.setVariables(data);
		String htmlContent = templateEngine.process(type, context);

		// Convert HTML to PDF
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString(htmlContent);
		renderer.layout();
		renderer.createPDF(outputStream);
		renderer.finishPDF();

		// Create response
		return new ByteArrayResource(outputStream.toByteArray());

	}

}

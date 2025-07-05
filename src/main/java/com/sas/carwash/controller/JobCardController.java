package com.sas.carwash.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sas.carwash.entity.JobCard;
import com.sas.carwash.entity.JobSpares;
import com.sas.carwash.entity.JobVehiclePhotos;
import com.sas.carwash.model.FastJobCardRecord;
import com.sas.carwash.model.FullJobCardRecord;
import com.sas.carwash.service.JobCardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/jobCard")
@RequiredArgsConstructor
@Slf4j
public class JobCardController {

	private final JobCardService jobCardService;

	@GetMapping
	public List<?> findAll() {
		return jobCardService.findAll();
	}

	@PostMapping
	public JobCard save(@RequestBody FullJobCardRecord fullJobCard) throws Exception {
		return jobCardService.saveFullJobCard(fullJobCard);
	}

	@PostMapping("/fastjobCard")
	public JobCard createFastJobCard(@RequestBody FastJobCardRecord jobCard) throws Exception {
		return jobCardService.createFastJobCard(jobCard);
	}

	@GetMapping("/status/{status}")
	public List<?> findAllByJobStatus(@PathVariable String status) {
		return jobCardService.findAllByJobStatus(status);
	}

	@PutMapping
	public ResponseEntity<?> update(@RequestBody JobCard jobCard) {
		try {
			return ResponseEntity.ok().body(jobCardService.update(jobCard));
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
	}

	@PutMapping("/jobStatus")
	public ResponseEntity<?> updateJobStatus(@RequestBody JobCard jobCard) {
		try {
			return ResponseEntity.ok().body(jobCardService.updateJobStatus(jobCard));
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
	}

	@GetMapping("/jobSpares/{id}")
	public JobSpares getJobSpares(@PathVariable String id) {
		return jobCardService.getJobSpares(id);
	}

	@PostMapping("/jobSpares")
	public ResponseEntity<?> updateJobSpares(@RequestBody JobSpares jobSparesInfo) {
		try {
			return ResponseEntity.ok().body(jobCardService.updateJobSpares(jobSparesInfo));
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
	}

	@GetMapping("/pdf/{id}")
	public ResponseEntity<?> generateJobCardPdf(@PathVariable String id) {

		try {
			return jobCardService.generateJobCardPdf(id);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@GetMapping("/billPdf/{id}")
	public ResponseEntity<?> generateBillPdf(@PathVariable String id) {

		try {
			return jobCardService.downloadBillPdf(id);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@PostMapping("/uploadPhotos/{id}")
	public ResponseEntity<?> uploadPhotos(@RequestParam("file") MultipartFile zipFile, @PathVariable String id) {
		try {
			// Save the zip file to MongoDB
			return ResponseEntity.ok(jobCardService.saveZipToMongo(zipFile, id));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error uploading photos: " + e.getMessage());
		}
	}

	@GetMapping("/getPhotos/{id}")
	public ResponseEntity<?> getPhotos(@PathVariable String id) {
		try {
			JobVehiclePhotos photoDoc = jobCardService.getZipPhotos(id);
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(photoDoc.getContentType()))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + photoDoc.getName() + "\"")
					.body(photoDoc.getContent());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error uploading photos: " + e.getMessage());
		}
	}

	@GetMapping("/getPhotoUrl/{id}")
	public Map<String, String> getPhotoUrl(@PathVariable String id) {
		return jobCardService.getPhotoUrl(id);
	}
}

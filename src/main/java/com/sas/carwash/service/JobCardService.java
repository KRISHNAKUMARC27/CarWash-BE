package com.sas.carwash.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;
import java.util.Calendar;

import org.springframework.scheduling.annotation.Scheduled;
import com.sas.carwash.entity.Estimate;
import com.sas.carwash.entity.Invoice;
import com.sas.carwash.entity.JobCard;
import com.sas.carwash.entity.JobCardCounters;
import com.sas.carwash.entity.JobSpares;
import com.sas.carwash.entity.JobSparesInfo;
import com.sas.carwash.entity.JobVehiclePhotos;
import com.sas.carwash.entity.Payments;
import com.sas.carwash.entity.ServiceInventory;
import com.sas.carwash.entity.SparesInventory;
import com.sas.carwash.model.CreditPayment;
import com.sas.carwash.model.FastJobCardRecord;
import com.sas.carwash.model.PaymentSplit;
import com.sas.carwash.repository.EstimateRepository;
import com.sas.carwash.repository.InvoiceRepository;
import com.sas.carwash.repository.JobCardRepository;
import com.sas.carwash.repository.JobSparesRepository;
import com.sas.carwash.repository.JobVehiclePhotosRepository;
import com.sas.carwash.repository.ServiceInventoryRepository;
import com.sas.carwash.repository.SparesInventoryRepository;
import com.sas.carwash.utils.NumberToWordsConverter;
import com.sas.carwash.utils.PdfUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobCardService {

	private final JobCardRepository jobCardRepository;
	private final JobSparesRepository jobSparesRepository;
	private final JobVehiclePhotosRepository jobVehiclePhotosRepository;
	private final ServiceInventoryRepository serviceInventoryRepository;
	private final SparesInventoryRepository sparesInventoryRepository;
	private final SparesService sparesService;
	private final InvoiceRepository invoiceRepository;
	private final EstimateRepository estimateRepository;
	private final MongoTemplate mongoTemplate;
	private final PdfUtils pdfUtils;
	private final PaymentsService paymentsService;
	private final EstimateService estimateService;
	private final InvoiceService invoiceService;
	private final UtilService utilService;

	@Value("${server.port}")
	private String serverPort;

	float rowHeight = 18f;

	public int getNextSequence(String sequenceName) {
		// Find the counter document and increment its sequence_value atomically
		Query query = new Query(Criteria.where("_id").is(sequenceName));
		Update update = new Update().inc("sequenceValue", 1);
		FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.options().returnNew(true);

		JobCardCounters counter = mongoTemplate.findAndModify(query, update, findAndModifyOptions,
				JobCardCounters.class);

		if (counter == null) {
			throw new RuntimeException("Error getting next sequence for " + sequenceName);
		}

		return counter.getSequenceValue();
	}

	public List<?> findAll() {
		return jobCardRepository.findAllByOrderByIdDesc();
	}

	public JobCard save(JobCard jobCard) {
		jobCard.setJobId(utilService.getNextJobCardIdSequenceAsInteger("jobCardId"));
		jobCard.setJobCreationDate(LocalDateTime.now());
		if (jobCard.getKiloMeters() != null) {
			jobCard.setNextFreeCheckKms(jobCard.getKiloMeters() + 1500);
			jobCard.setNextServiceKms(jobCard.getKiloMeters() + 3000);
		}
		jobCard = jobCardRepository.save(jobCard);

		sendNotifications("JobCard opened - " + jobCard.getJobId(), jobCard.toString());
		return jobCard;
	}

	public List<?> findAllByJobStatus(String status) {
		return jobCardRepository.findAllByJobStatusOrderByIdDesc(status);
	}

	public JobCard update(JobCard jobCard) {
		if (jobCard.getKiloMeters() != null) {
			jobCard.setNextFreeCheckKms(jobCard.getKiloMeters() + 1500);
			jobCard.setNextServiceKms(jobCard.getKiloMeters() + 3000);
		}
		return jobCardRepository.save(jobCard);
	}

	// DIFFING LOGIC IS COMMENTED

	// @Transactional(rollbackFor = Exception.class)
	// public synchronized JobSpares updateJobSpares(JobSpares jobSpares) throws
	// Exception {
	//
	// JobSpares origJobSpares =
	// jobSparesRepository.findById(jobSpares.getId()).orElse(null);
	// // List<JobSparesInfo> origJobSparesInfoList =
	// origJobSpares.getJobSparesInfo();
	//
	// List<JobSparesInfo> jobSparesInfoList = jobSpares.getJobSparesInfo();
	//
	// // Checking for quantity insufficiency
	// for (JobSparesInfo jobSparesInfo : jobSparesInfoList) {
	// SparesInventory spares = sparesService.findById(jobSparesInfo.getSparesId());
	// if (spares != null) {
	// if (origJobSpares != null) {
	// List<JobSparesInfo> origJobSparesInfoList = origJobSpares.getJobSparesInfo();
	// Optional<JobSparesInfo> origJobSparesInfoOpt = origJobSparesInfoList.stream()
	// .filter(info ->
	// info.getSparesId().equals(jobSparesInfo.getSparesId())).findFirst();
	//
	// if (origJobSparesInfoOpt.isPresent()) {
	// BigDecimal originalQty = origJobSparesInfoOpt.get().getQty();
	// spares.setQty(spares.getQty().add(originalQty));
	// }
	// }
	// if (jobSparesInfo.getQty().compareTo(spares.getQty()) > 0) {
	// throw new Exception("Quantity of " + spares.getDesc() + " in Spares inventory
	// (" + spares.getQty()
	// + ") is lesser than quantity of spares used for job (" +
	// jobSparesInfo.getQty() + ")");
	//
	// }
	// }
	// }
	//
	// String exceptionMess = "";
	//
	// if (origJobSpares != null) {
	// List<JobSparesInfo> origJobSparesInfoList = origJobSpares.getJobSparesInfo();
	// // some jobspares are deleted. hence add those back to jobSpares
	// List<JobSparesInfo> deletedJobSparesInfoList = new
	// ArrayList<>(origJobSparesInfoList);
	// deletedJobSparesInfoList.removeAll(jobSparesInfoList);
	//
	// for (JobSparesInfo deletedJobSparesInfo : deletedJobSparesInfoList) {
	// SparesInventory deletedSpares =
	// sparesService.findById(deletedJobSparesInfo.getSparesId());
	// if (deletedSpares != null) {
	// BigDecimal result =
	// deletedSpares.getQty().add(deletedJobSparesInfo.getQty());
	// deletedSpares.setQty(result);
	// deletedSpares.setAmount(deletedSpares.getSellRate().multiply(result));
	// try {
	// sparesService.saveFromJobSpares(deletedSpares);
	// } catch (OptimisticLockingFailureException e) {
	// exceptionMess += " Please retry!! Concurrent modification detected while
	// updating deletedJobSpares for "
	// + deletedSpares.getDesc();
	// // throw new Exception("Please retry!! Concurrent modification detected for "
	// +
	// // spares.getDesc());
	// }
	// } else {
	// exceptionMess += " " + deletedJobSparesInfo.getSparesAndLabour()
	// + " is not found in Spares Inventory ";
	// }
	// }
	// // }
	// }
	//
	// // test for empty scenario TODO
	//
	// for (JobSparesInfo jobSparesInfo : jobSparesInfoList) {
	// SparesInventory spares = sparesService.findById(jobSparesInfo.getSparesId());
	// if (spares != null) {
	//
	// // Since jobSpares object from FE contains entire object, check for values
	// // present and add them.
	// if (origJobSpares != null) {
	// List<JobSparesInfo> origJobSparesInfoList = origJobSpares.getJobSparesInfo();
	// Optional<JobSparesInfo> origJobSparesInfoOpt = origJobSparesInfoList.stream()
	// .filter(info ->
	// info.getSparesId().equals(jobSparesInfo.getSparesId())).findFirst();
	//
	// if (origJobSparesInfoOpt.isPresent()) {
	// BigDecimal originalQty = origJobSparesInfoOpt.get().getQty();
	// spares.setQty(spares.getQty().add(originalQty));
	// }
	// }
	// if (jobSparesInfo.getQty().compareTo(spares.getQty()) > 0) {
	// exceptionMess += " Quantity of " + spares.getDesc() + " in Spares inventory
	// (" + spares.getQty()
	// + ") is lesser than quantity of spares used for job (" +
	// jobSparesInfo.getQty() + ") ";
	//
	// } else {
	// BigDecimal result = spares.getQty().subtract(jobSparesInfo.getQty());
	// spares.setQty(result);
	// spares.setAmount(spares.getSellRate().multiply(result));
	// try {
	// sparesService.saveFromJobSpares(spares);
	// } catch (OptimisticLockingFailureException e) {
	// exceptionMess += " Please retry!! Concurrent modification detected for " +
	// spares.getDesc();
	// // throw new Exception("Please retry!! Concurrent modification detected for "
	// +
	// // spares.getDesc());
	// }
	// }
	//
	// } else {
	// exceptionMess += " " + jobSparesInfo.getSparesAndLabour() + " is not found in
	// Spares Inventory ";
	// // throw new Exception(jobSparesInfo.getSparesAndLabour() + " is not found in
	// // Spares Inventory ");
	// // should never come here
	// }
	// }
	// if (!exceptionMess.isEmpty()) {
	// jobSparesRepository.save(jobSpares);
	// // This will cause the transaction to roll back
	// throw new Exception(exceptionMess);
	// }
	// return jobSparesRepository.save(jobSpares);
	// }

	public synchronized JobSpares updateJobSpares(JobSpares jobSpares) throws Exception {

		// Fetch original job spares from DB
		JobSpares origJobSpares = jobSparesRepository.findById(jobSpares.getId()).orElse(null);
		List<JobSparesInfo> jobSparesInfoList = jobSpares.getJobSparesInfo();

		Map<String, BigDecimal> previousQuantities = new HashMap<>();

		try {
			// Loop through the incoming job spares list
			for (JobSparesInfo jobSparesInfo : jobSparesInfoList) {
				SparesInventory spares = sparesService.findById(jobSparesInfo.getSparesId());
				SparesInventory origspares = sparesService.findById(jobSparesInfo.getSparesId());
				if (spares == null) {
					throw new Exception("Spares ID " + jobSparesInfo.getSparesId() + " not found in inventory.");
				}

				// Update the units field from misc2
				if (spares.getUnits() != null) {
					jobSparesInfo.setUnits(spares.getUnits());
				}
				// Handle ADD action
				if ("ADD".equals(jobSparesInfo.getAction())) {
					if (jobSparesInfo.getQty().compareTo(spares.getQty()) > 0) {
						throw new Exception("Insufficient stock for spare " + spares.getDesc());
					}
					// Deduct the quantity
					spares.setQty(spares.getQty().subtract(jobSparesInfo.getQty()));
					spares.setAmount(spares.getSellRate().multiply(spares.getQty()));
					sparesService.saveFromJobSpares(spares);
					previousQuantities.put(origspares.getId(), origspares.getQty());

					// Handle MODIFY action
				} else if ("MODIFY".equals(jobSparesInfo.getAction())) {
					Optional<JobSparesInfo> origJobSparesInfoOpt = origJobSpares != null
							? origJobSpares.getJobSparesInfo().stream()
									.filter(info -> info.getSparesId().equals(jobSparesInfo.getSparesId())).findFirst()
							: Optional.empty();

					if (origJobSparesInfoOpt.isPresent()) {
						BigDecimal originalQty = origJobSparesInfoOpt.get().getQty();
						BigDecimal qtyDiff = jobSparesInfo.getQty().subtract(originalQty);

						// Adjust inventory based on qty difference
						if (qtyDiff.compareTo(BigDecimal.ZERO) > 0) { // Increasing quantity
							if (qtyDiff.compareTo(spares.getQty()) > 0) {
								throw new Exception("Insufficient stock for spare " + spares.getDesc());
							}
							spares.setQty(spares.getQty().subtract(qtyDiff));
						} else { // Reducing quantity
							spares.setQty(spares.getQty().add(qtyDiff.abs()));
						}
						spares.setAmount(spares.getSellRate().multiply(spares.getQty()));
						sparesService.saveFromJobSpares(spares);
						previousQuantities.put(origspares.getId(), origspares.getQty());
					} else {
						throw new Exception("Original spare not found for MODIFY action for spare ID "
								+ jobSparesInfo.getSparesId());
					}

					// Handle DELETE action
				} else if ("DELETE".equals(jobSparesInfo.getAction())) {
					// Find the original spare in the job spares (to get the correct original
					// quantity)
					Optional<JobSparesInfo> origJobSparesInfoOpt = origJobSpares.getJobSparesInfo().stream()
							.filter(info -> info.getSparesId().equals(jobSparesInfo.getSparesId())).findFirst();

					if (origJobSparesInfoOpt.isPresent()) {
						BigDecimal originalQty = origJobSparesInfoOpt.get().getQty();
						// Add the original quantity back to inventory
						spares.setQty(spares.getQty().add(originalQty));
						spares.setAmount(spares.getSellRate().multiply(spares.getQty()));
						sparesService.saveFromJobSpares(spares);
						previousQuantities.put(origspares.getId(), origspares.getQty());
					} else {
						throw new Exception("Original spare not found for DELETE action for spare ID "
								+ jobSparesInfo.getSparesId());
					}
				}

			}

			// **Filter out the job spares marked for DELETE** before saving
			List<JobSparesInfo> filteredJobSparesInfoList = jobSparesInfoList.stream()
					.filter(jobSparesInfo -> !"DELETE".equals(jobSparesInfo.getAction())).collect(Collectors.toList());

			// Update the job spares list and set action to null
			filteredJobSparesInfoList.forEach(jobSparesInfo -> jobSparesInfo.setAction(null));
			jobSpares.setJobSparesInfo(filteredJobSparesInfoList);

		} catch (Exception ex) {
			for (Map.Entry<String, BigDecimal> entry : previousQuantities.entrySet()) {
				SparesInventory spares = sparesService.findById(entry.getKey());
				spares.setQty(entry.getValue());
				spares.setAmount(spares.getSellRate().multiply(spares.getQty()));
				sparesService.saveFromJobSpares(spares);
			}
			throw new Exception("Rolled back changes. Error: " + ex.getMessage());

		}

		jobSpares = calculateGrandTotal(jobSpares);
		jobSpares = calculateGSTAndUpdateFields(jobSpares);
		jobSpares = jobSparesRepository.save(jobSpares);

		// CHECK IF INVOICE ID OR ESTIMATE ID IS SET. If yes, then re-estimate them.
		if (jobSpares.getEstimateObjId() != null)
			recalculateAndUpdateEstimate(jobSpares);
		else if (jobSpares.getInvoiceObjId() != null)
			recalculateAndUpdateInvoice(jobSpares);

		return jobSpares;
	}

	private JobSpares calculateGSTAndUpdateFields(JobSpares jobSpares) {

		for (JobSparesInfo jobSparesInfo : jobSpares.getJobServiceInfo()) {
			ServiceInventory service = serviceInventoryRepository.findById(jobSparesInfo.getSparesId()).orElseThrow(
					() -> new RuntimeException(jobSparesInfo.getSparesId() + " not found during gst calc"));
			jobSparesInfo.setGstPercentage(service.getCgst());
			BigDecimal totalGstPercentage = service.getCgst().add(service.getSgst());
			BigDecimal totalGstAmount = jobSparesInfo.getAmount().multiply(totalGstPercentage)
					.divide(BigDecimal.valueOf(100)).add(jobSparesInfo.getAmount());
			jobSparesInfo.setGstAmount(totalGstAmount);
		}
		for (JobSparesInfo jobSparesInfo : jobSpares.getJobSparesInfo()) {
			SparesInventory service = sparesInventoryRepository.findById(jobSparesInfo.getSparesId()).orElseThrow(
					() -> new RuntimeException(jobSparesInfo.getSparesId() + " not found during gst calc"));
			jobSparesInfo.setGstPercentage(service.getCgst());
			BigDecimal totalGstPercentage = service.getCgst().add(service.getSgst());
			BigDecimal totalGstAmount = jobSparesInfo.getAmount().multiply(totalGstPercentage)
					.divide(BigDecimal.valueOf(100)).add(jobSparesInfo.getAmount());
			jobSparesInfo.setGstAmount(totalGstAmount);
		}

		BigDecimal totalGstSparesValue = jobSpares.getJobSparesInfo() != null
				? jobSpares.getJobSparesInfo().stream().map(JobSparesInfo::getGstAmount).filter(Objects::nonNull)
						.reduce(BigDecimal.ZERO, BigDecimal::add)
				: BigDecimal.ZERO;

		BigDecimal totalGstServiceValue = jobSpares.getJobServiceInfo() != null
				? jobSpares.getJobServiceInfo().stream().map(JobSparesInfo::getGstAmount).filter(Objects::nonNull)
						.reduce(BigDecimal.ZERO, BigDecimal::add)
				: BigDecimal.ZERO;

		BigDecimal grandGstTotal = totalGstSparesValue.add(totalGstServiceValue);
		jobSpares.setGrandTotalWithGST(grandGstTotal);
		jobSpares.setTotalSparesValueWithGST(totalGstSparesValue);
		jobSpares.setTotalServiceValueWithGST(totalGstServiceValue);

		return jobSpares;
	}

	// public JobSpares getJobSpares(String id) {
	// return
	// jobSparesRepository.findById(id).orElse(JobSpares.builder().jobSparesInfo(new
	// ArrayList<>())
	// .jobConsumablesInfo(new ArrayList<>()).jobLaborInfo(new
	// ArrayList<>()).build());
	// }

	public JobSpares getJobSpares(String id) {
		JobSpares jobSpares = jobSparesRepository.findById(id)
				.orElse(JobSpares.builder().jobSparesInfo(new ArrayList<>()).jobServiceInfo(new ArrayList<>()).build());

		// Ensure non-null lists
		if (jobSpares.getJobSparesInfo() == null) {
			jobSpares.setTotalSparesValue(BigDecimal.ZERO);
			jobSpares.setJobSparesInfo(new ArrayList<>());
		}
		if (jobSpares.getJobServiceInfo() == null) {
			jobSpares.setTotalServiceValue(BigDecimal.ZERO);
			jobSpares.setJobServiceInfo(new ArrayList<>());
		}

		return jobSpares;
	}

	public synchronized JobCard updateJobStatus(JobCard jobCard) throws Exception {
		JobCard origJobCard = jobCardRepository.findById(jobCard.getId()).orElse(null);
		if (origJobCard != null && !origJobCard.getJobStatus().equals("CLOSED")) {
			if (jobCard.getJobStatus().equals("CLOSED")) {
				LocalDateTime jobCloseDate = LocalDateTime.now();
				if (origJobCard.getJobCloseDate() == null)
					origJobCard.setJobCloseDate(jobCloseDate);

				JobSpares origJobSpares = jobSparesRepository.findById(jobCard.getId()).orElse(null);
				if (origJobSpares != null) {
					if (origJobSpares.getJobCloseDate() == null)
						origJobSpares.setJobCloseDate(jobCloseDate);

					calculateTotals(origJobSpares);
					jobSparesRepository.save(origJobSpares);
				}
			}
		} else {
			// throw new Exception("Invalid jobCard or JobCard already Closed " +
			// jobCard.getJobId());
			// should never come here
		}
		origJobCard.setJobStatus(jobCard.getJobStatus());

		sendNotifications("JobCard - " + jobCard.getJobId() + " status " + jobCard.getJobStatus(), jobCard.toString());

		return jobCardRepository.save(origJobCard);
	}

	// private void calculateTotals(JobSpares origJobSpares) throws Exception {
	// BigDecimal totalSparesValue =
	// origJobSpares.getJobSparesInfo().stream().map(JobSparesInfo::getAmount)
	// .filter(amount -> amount != null) // Ensure no null values are encountered
	// .reduce(BigDecimal.ZERO, BigDecimal::add);
	//
	// BigDecimal totalLabourValue =
	// origJobSpares.getJobLaborInfo().stream().map(JobSparesInfo::getAmount)
	// .filter(amount -> amount != null) // Ensure no null values are encountered
	// .reduce(BigDecimal.ZERO, BigDecimal::add);
	//
	// BigDecimal totalConsumablesValue =
	// origJobSpares.getJobConsumablesInfo().stream().map(JobSparesInfo::getAmount)
	// .filter(amount -> amount != null) // Ensure no null values are encountered
	// .reduce(BigDecimal.ZERO, BigDecimal::add);
	//
	// BigDecimal grandTotal =
	// totalSparesValue.add(totalLabourValue).add(totalConsumablesValue);
	//
	// if (!grandTotal.equals(origJobSpares.getGrandTotal())) {
	// throw new Exception("Total amount calculation is wrong in UI");
	// }
	// }

	private JobSpares calculateGrandTotal(JobSpares origJobSpares) {
		// Calculate total spares value
		BigDecimal totalSparesValue = origJobSpares.getJobSparesInfo() != null
				? origJobSpares.getJobSparesInfo().stream().map(JobSparesInfo::getAmount).filter(Objects::nonNull)
						.reduce(BigDecimal.ZERO, BigDecimal::add)
				: BigDecimal.ZERO;

		// Calculate total Service value
		BigDecimal totalServiceValue = origJobSpares.getJobServiceInfo() != null
				? origJobSpares.getJobServiceInfo().stream().map(JobSparesInfo::getAmount).filter(Objects::nonNull)
						.reduce(BigDecimal.ZERO, BigDecimal::add)
				: BigDecimal.ZERO;

		// Calculate the grand total
		BigDecimal grandTotal = totalSparesValue.add(totalServiceValue);
		origJobSpares.setGrandTotal(grandTotal);
		origJobSpares.setTotalServiceValue(totalServiceValue);
		origJobSpares.setTotalSparesValue(totalSparesValue);
		return origJobSpares;
	}

	private void calculateTotals(JobSpares origJobSpares) throws Exception {
		// Calculate total spares value
		BigDecimal totalSparesValue = origJobSpares.getJobSparesInfo() != null
				? origJobSpares.getJobSparesInfo().stream().map(JobSparesInfo::getAmount).filter(Objects::nonNull)
						.reduce(BigDecimal.ZERO, BigDecimal::add)
				: BigDecimal.ZERO;

		// Calculate total Service value
		BigDecimal totalServiceValue = origJobSpares.getJobServiceInfo() != null
				? origJobSpares.getJobServiceInfo().stream().map(JobSparesInfo::getAmount).filter(Objects::nonNull)
						.reduce(BigDecimal.ZERO, BigDecimal::add)
				: BigDecimal.ZERO;

		// Calculate the grand total
		BigDecimal grandTotal = totalSparesValue.add(totalServiceValue);
		origJobSpares.setGrandTotal(grandTotal);
		origJobSpares.setTotalServiceValue(totalServiceValue);
		origJobSpares.setTotalSparesValue(totalSparesValue);

		// Validate the grand total//TODO CHECK IF BELOW LINE IS CORRECT ??
		if (origJobSpares.getGrandTotal() != null && !grandTotal.equals(origJobSpares.getGrandTotal())) {
			throw new Exception("Total amount calculation is wrong in UI");
		}

		BigDecimal totalGstSparesValue = origJobSpares.getJobSparesInfo() != null
				? origJobSpares.getJobSparesInfo().stream().map(JobSparesInfo::getGstAmount).filter(Objects::nonNull)
						.reduce(BigDecimal.ZERO, BigDecimal::add)
				: BigDecimal.ZERO;

		BigDecimal totalGstServiceValue = origJobSpares.getJobServiceInfo() != null
				? origJobSpares.getJobServiceInfo().stream().map(JobSparesInfo::getGstAmount).filter(Objects::nonNull)
						.reduce(BigDecimal.ZERO, BigDecimal::add)
				: BigDecimal.ZERO;

		BigDecimal grandGstTotal = totalGstSparesValue.add(totalGstServiceValue);
		origJobSpares.setGrandTotalWithGST(grandGstTotal);
		origJobSpares.setTotalSparesValueWithGST(totalGstSparesValue);
		origJobSpares.setTotalServiceValueWithGST(totalGstServiceValue);
	}

	public ResponseEntity<?> generateJobCardPdf(String id) throws Exception {

		// JobCard jobCard = jobCardRepository.findById(id).orElse(null);
		// JobSpares jobSpares = jobSparesRepository.findById(id).orElse(null);
		//
		// if (jobCard == null) {
		// throw new Exception("JobCard not found for id " + id);
		// // should never get here.
		// }
		//
		// if (jobSpares == null) {
		// throw new Exception("JobSpares not found for id " + id);
		// }
		//
		// // Create table with varying columns for different rows
		// Table table = new Table(UnitValue.createPercentArray(new float[] { 30, 35, 35
		// }));
		// table.setWidth(UnitValue.createPercentValue(100)); // Set the table width to
		// 100%
		//
		// Image image = new Image(ImageDataFactory.create("classpath:jm_logo_1.jpeg"));
		// // Replace with the path to
		// // your
		// image.setMaxHeight(120);
		// image.setMaxWidth(150);// image
		// table.addCell(
		// new
		// Paragraph("").add(image).setVerticalAlignment(VerticalAlignment.MIDDLE).setKeepTogether(true));
		//
		// table.addCell(createCellWithFixedSpace("Job Card No: ",
		// stringNullCheck(jobCard.getJobId()), "\n", "Owner: ",
		// stringNullCheck(jobCard.getOwnerName()), "\n", "Contact No: ",
		// stringNullCheck(jobCard.getOwnerPhoneNumber())).setVerticalAlignment(VerticalAlignment.MIDDLE)
		// .setHorizontalAlignment(HorizontalAlignment.LEFT));
		//
		// table.addCell(createCellWithFixedSpace("Date: ",
		// createDateString(jobCard.getJobCreationDate()), "\n",
		// "Email: ", stringNullCheck(jobCard.getOwnerEmailId()), "\n", "Driver: ",
		// stringNullCheck(jobCard.getDriver())).setFontSize(11).setVerticalAlignment(VerticalAlignment.MIDDLE)
		// .setHorizontalAlignment(HorizontalAlignment.LEFT));
		//
		// Table singleColumnTable = new Table(UnitValue.createPercentArray(new float[]
		// { 100 }));
		// singleColumnTable.setWidth(UnitValue.createPercentValue(100));
		// singleColumnTable.addCell(createCellWithFixedSpace("Address: ",
		// stringNullCheck(jobCard.getOwnerAddress()))
		// .setFontSize(11).setVerticalAlignment(VerticalAlignment.MIDDLE)
		// .setHorizontalAlignment(HorizontalAlignment.LEFT));
		//
		// Table doubleColumnTable = new Table(UnitValue.createPercentArray(new float[]
		// { 50, 50 }));
		// doubleColumnTable.setWidth(UnitValue.createPercentValue(100));
		// Cell ColumnCell = new Cell()
		// .add(createCellWithFixedSpace("Vehicle Reg. No: ",
		// stringNullCheck(jobCard.getVehicleRegNo()), "\n",
		// "Vehicle Model: ", stringNullCheck(jobCard.getVehicleModel()), "\n",
		// "Technician Name: ",
		// stringNullCheck(jobCard.getTechnicianName())));
		//
		// doubleColumnTable.addCell(ColumnCell);
		//
		// Cell singleColumnCell1 = new Cell()
		// .add(createCellWithFixedSpace("Type of Vehicle: " +
		// stringNullCheck(jobCard.getVehicleName()), "\n",
		// "K.M: " + stringNullCheck(jobCard.getKiloMeters()), "\n",
		// "Vehicle Out Date: " + createDateString(jobCard.getVehicleOutDate())));
		// doubleColumnTable.addCell(singleColumnCell1);
		//
		// setMinWidth(table, 0, 1, 100f);
		//
		// Table table1 = new Table(UnitValue.createPercentArray(new float[] { 30, 20,
		// 20, 30 }));
		// table1.setWidth(UnitValue.createPercentValue(100));
		//
		// table1.addCell(new Cell().add(new Paragraph("ITEMS")).setBold());
		// Cell okCell = new Cell().add(new
		// Paragraph("OK")).setTextAlignment(TextAlignment.CENTER).setBold();
		// table1.addCell(okCell);
		// Cell notOkCell = new Cell().add(new Paragraph("NOT
		// OK")).setTextAlignment(TextAlignment.CENTER).setBold();
		// table1.addCell(notOkCell);
		// Image image1 = new
		// Image(ImageDataFactory.create("classpath:jm_scratch_pic.JPG"));
		//
		// image1.setMaxHeight(720);
		// image1.setMaxWidth(150);
		// image1.setPaddingLeft(1);// image
		//
		// table1.addCell(new Cell(11,
		// 1).add(image1).setVerticalAlignment(VerticalAlignment.MIDDLE).setPaddingLeft(20));
		//
		// table1.addCell(new Cell().add(new Paragraph("COVER")).setBold());
		// updateVechicleItems(table1, jobCard.getCover());
		//
		// table1.addCell(new Cell().add(new Paragraph("GLASS")).setBold());
		// updateVechicleItems(table1, jobCard.getGlass());
		//
		// table1.addCell(new Cell().add(new Paragraph("DASHBOARD & TOOLS")).setBold());
		// updateVechicleItems(table1, jobCard.getDashboardAndTools());
		//
		// table1.addCell(new Cell().add(new Paragraph("FUEL POINTS")).setBold());
		// table1.addCell(stringNullCheck(jobCard.getFuelPoints()));
		// table1.addCell("");
		//
		// table1.addCell(new Cell().add(new Paragraph("SPARE WHEEL")).setBold());
		// updateVechicleItems(table1, jobCard.getSpareWheel());
		//
		// table1.addCell(new Cell().add(new Paragraph("JACKEY HANDLES")).setBold());
		// updateVechicleItems(table1, jobCard.getJackeyHandles());
		//
		// table1.addCell(new Cell().add(new Paragraph("TOOL KITS")).setBold());
		// updateVechicleItems(table1, jobCard.getToolKits());
		//
		// table1.addCell(new Cell().add(new Paragraph("PEN DRIVE")).setBold());
		// updateVechicleItems(table1, jobCard.getPenDrive());
		//
		// table1.addCell(new Cell().add(new Paragraph("WHEEL CAP")).setBold());
		// updateVechicleItems(table1, jobCard.getWheelCap());
		//
		// table1.addCell(new Cell().add(new Paragraph("A/c GRILLS")).setBold());
		// updateVechicleItems(table1, jobCard.getAcGrills());
		//
		// Table table2 = new Table(UnitValue.createPercentArray(new float[] { 60, 20,
		// 20 }));
		// table2.setWidth(UnitValue.createPercentValue(100));
		//
		// Cell customerComplaintsCell = new Cell().add(new Paragraph("CUSTOMER
		// COMPLAINTS"))
		// .setTextAlignment(TextAlignment.CENTER).setBold();
		//
		// Cell completedCell = new Cell().add(new
		// Paragraph("COMPLETED")).setTextAlignment(TextAlignment.CENTER)
		// .setBold();
		//
		// Cell remarksCell = new Cell().add(new
		// Paragraph("REMARKS")).setTextAlignment(TextAlignment.CENTER).setBold();
		//
		// table2.addHeaderCell(customerComplaintsCell);
		// table2.addHeaderCell(completedCell);
		// table2.addHeaderCell(remarksCell);
		//
		// for (JobCardInfo jobInfo : jobCard.getJobInfo()) {
		// table2.addCell(stringNullCheck(jobInfo.getComplaints()));
		// table2.addCell(stringNullCheck(jobInfo.getCompleted()));
		// table2.addCell(stringNullCheck(jobInfo.getRemarks()));
		// }
		//
		// Table table3 = null;
		// if (jobSpares != null) {
		// table3 = new Table(UnitValue.createPercentArray(new float[] { 50, 10, 20, 30
		// }));
		// table3.setWidth(UnitValue.createPercentValue(100));
		// if (jobSpares.getJobSparesInfo() != null &&
		// jobSpares.getJobSparesInfo().get(0).getSparesAndLabour() != null) {
		//
		// Cell sparesCell = new Cell().add(new
		// Paragraph("Spares")).setTextAlignment(TextAlignment.CENTER)
		// .setBold();
		//
		// Cell qtySparesCell = new Cell().add(new
		// Paragraph("Qty.")).setTextAlignment(TextAlignment.CENTER)
		// .setBold();
		//
		// Cell rateSparesCell = new Cell().add(new
		// Paragraph("Rate")).setTextAlignment(TextAlignment.CENTER)
		// .setBold();
		//
		// Cell amountSparesCell = new Cell().add(new
		// Paragraph("Amount")).setTextAlignment(TextAlignment.CENTER)
		// .setBold();
		//
		// table3.addCell(sparesCell);
		// table3.addCell(qtySparesCell);
		// table3.addCell(rateSparesCell);
		// table3.addCell(amountSparesCell);
		// for (JobSparesInfo jobSparesInfo : jobSpares.getJobSparesInfo()) {
		// table3.addCell(stringNullCheck(jobSparesInfo.getSparesAndLabour()));
		// table3.addCell(new Cell().add(new
		// Paragraph(jobSparesInfo.getQty().toString()))
		// .setTextAlignment(TextAlignment.RIGHT));
		// table3.addCell(new Cell().add(new
		// Paragraph(jobSparesInfo.getRate().toString()))
		// .setTextAlignment(TextAlignment.RIGHT));
		// table3.addCell(new Cell().add(new
		// Paragraph(jobSparesInfo.getAmount().toString()))
		// .setTextAlignment(TextAlignment.RIGHT));
		// }
		// }
		// if (jobSpares.getJobConsumablesInfo() != null &&
		// jobSpares.getJobConsumablesInfo().get(0).getSparesAndLabour() != null) {
		// Cell labourCell = new Cell().add(new
		// Paragraph("Consumables")).setTextAlignment(TextAlignment.CENTER)
		// .setBold();
		// Cell qtyLabourCell = new Cell().add(new
		// Paragraph("Qty.")).setTextAlignment(TextAlignment.CENTER)
		// .setBold();
		// Cell rateLabourCell = new Cell().add(new
		// Paragraph("Rate")).setTextAlignment(TextAlignment.CENTER)
		// .setBold();
		// Cell amountLabourCell = new Cell().add(new
		// Paragraph("Amount")).setTextAlignment(TextAlignment.CENTER)
		// .setBold();
		// table3.addCell(labourCell);
		// table3.addCell(qtyLabourCell);
		// table3.addCell(rateLabourCell);
		// table3.addCell(amountLabourCell);
		//
		// for (JobSparesInfo jobLaborInfo : jobSpares.getJobConsumablesInfo()) {
		// table3.addCell(stringNullCheck(jobLaborInfo.getSparesAndLabour()));
		// table3.addCell(new Cell().add(new
		// Paragraph("")).setTextAlignment(TextAlignment.RIGHT));
		// table3.addCell(new Cell().add(new
		// Paragraph("")).setTextAlignment(TextAlignment.RIGHT));
		// table3.addCell(new Cell().add(new
		// Paragraph(jobLaborInfo.getAmount().toString()))
		// .setTextAlignment(TextAlignment.RIGHT));
		// }
		// }
		// if (jobSpares.getJobLaborInfo() != null &&
		// jobSpares.getJobLaborInfo().get(0).getSparesAndLabour() != null) {
		// Cell labourCell = new Cell().add(new
		// Paragraph("Labour")).setTextAlignment(TextAlignment.CENTER)
		// .setBold();
		// Cell qtyLabourCell = new Cell().add(new
		// Paragraph("Qty.")).setTextAlignment(TextAlignment.CENTER)
		// .setBold();
		// Cell rateLabourCell = new Cell().add(new
		// Paragraph("Rate")).setTextAlignment(TextAlignment.CENTER)
		// .setBold();
		// Cell amountLabourCell = new Cell().add(new
		// Paragraph("Amount")).setTextAlignment(TextAlignment.CENTER)
		// .setBold();
		// table3.addCell(labourCell);
		// table3.addCell(qtyLabourCell);
		// table3.addCell(rateLabourCell);
		// table3.addCell(amountLabourCell);
		//
		// for (JobSparesInfo jobLaborInfo : jobSpares.getJobLaborInfo()) {
		// table3.addCell(stringNullCheck(jobLaborInfo.getSparesAndLabour()));
		// table3.addCell(new Cell().add(new
		// Paragraph(jobLaborInfo.getQty().toString()))
		// .setTextAlignment(TextAlignment.RIGHT));
		// table3.addCell(new Cell().add(new
		// Paragraph(jobLaborInfo.getRate().toString()))
		// .setTextAlignment(TextAlignment.RIGHT));
		// table3.addCell(new Cell().add(new
		// Paragraph(jobLaborInfo.getAmount().toString()))
		// .setTextAlignment(TextAlignment.RIGHT));
		// }
		// }
		// if (jobSpares.getJobExternalWorkInfo() != null &&
		// jobSpares.getJobExternalWorkInfo().get(0).getSparesAndLabour() != null) {
		// Cell externalWorkCell = new Cell().add(new Paragraph("ExternalWork"))
		// .setTextAlignment(TextAlignment.CENTER).setBold();
		// Cell qtyExternalWorkCell = new Cell().add(new
		// Paragraph("Qty.")).setTextAlignment(TextAlignment.CENTER)
		// .setBold();
		// Cell rateExternalWorkCell = new Cell().add(new
		// Paragraph("Rate")).setTextAlignment(TextAlignment.CENTER)
		// .setBold();
		// Cell amountExternalWorkCell = new Cell().add(new Paragraph("Amount"))
		// .setTextAlignment(TextAlignment.CENTER).setBold();
		// table3.addCell(externalWorkCell);
		// table3.addCell(qtyExternalWorkCell);
		// table3.addCell(rateExternalWorkCell);
		// table3.addCell(amountExternalWorkCell);
		//
		// for (JobSparesInfo jobExternalWorkInfo : jobSpares.getJobExternalWorkInfo())
		// {
		// table3.addCell(stringNullCheck(jobExternalWorkInfo.getSparesAndLabour()));
		// table3.addCell(new Cell().add(
		// new Paragraph(jobExternalWorkInfo.getQty() != null ?
		// jobExternalWorkInfo.getQty().toString()
		// : BigDecimal.ZERO.toString()))
		// .setTextAlignment(TextAlignment.RIGHT));
		// table3.addCell(new Cell().add(new Paragraph(
		// jobExternalWorkInfo.getRate() != null ?
		// jobExternalWorkInfo.getRate().toString()
		// : BigDecimal.ZERO.toString()))
		// .setTextAlignment(TextAlignment.RIGHT));
		// table3.addCell(new Cell().add(new Paragraph(
		// jobExternalWorkInfo.getAmount() != null ?
		// jobExternalWorkInfo.getAmount().toString()
		// : BigDecimal.ZERO.toString()))
		// .setTextAlignment(TextAlignment.RIGHT));
		// }
		// }
		//
		// table3.addCell(new Cell(1, 2).add(new Paragraph("Total Spares
		// Value")).setTextAlignment(TextAlignment.RIGHT)
		// .setBold());
		// table3.addCell(new Cell(1, 2).add(new
		// Paragraph(stringNullCheck(jobSpares.getTotalSparesValue()))
		// .setTextAlignment(TextAlignment.RIGHT)));
		// table3.addCell(new Cell(1, 2).add(new Paragraph("Total Consumables Value"))
		// .setTextAlignment(TextAlignment.RIGHT).setBold());
		// table3.addCell(new Cell(1, 2).add(new
		// Paragraph(stringNullCheck(jobSpares.getTotalConsumablesValue()))
		// .setTextAlignment(TextAlignment.RIGHT)));
		//
		// table3.addCell(new Cell(1, 2).add(new Paragraph("Total Labour
		// Value")).setTextAlignment(TextAlignment.RIGHT)
		// .setBold());
		// table3.addCell(new Cell(1, 2).add(new
		// Paragraph(stringNullCheck(jobSpares.getTotalLabourValue()))
		// .setTextAlignment(TextAlignment.RIGHT)));
		//
		// table3.addCell(new Cell(1, 2).add(new Paragraph("Total ExternalWork Value"))
		// .setTextAlignment(TextAlignment.RIGHT).setBold());
		// table3.addCell(new Cell(1, 2).add(new
		// Paragraph(stringNullCheck(jobSpares.getTotalExternalWorkValue()))
		// .setTextAlignment(TextAlignment.RIGHT)));
		//
		// table3.addCell(
		// new Cell(1, 2).add(new Paragraph("Grand
		// Total")).setTextAlignment(TextAlignment.RIGHT).setBold());
		// table3.addCell(new Cell(1, 2).add(
		// new
		// Paragraph(stringNullCheck(jobSpares.getGrandTotal())).setTextAlignment(TextAlignment.RIGHT)));
		// }
		//
		//// try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		//// PdfWriter pdfWriter = new PdfWriter(outputStream);
		//// PdfDocument pdfDocument = new PdfDocument(pdfWriter);
		//// Document document = new Document(pdfDocument)) {
		////
		//// document.add(table);
		//// document.add(singleColumnTable);
		//// document.add(doubleColumnTable);
		//// document.add(table1);
		//// document.add(table2);
		//// if (table3 != null) {
		//// document.add(table3);
		//// }
		//// ByteArrayResource resource = new
		// ByteArrayResource(outputStream.toByteArray());
		//// String filename = jobCard.getJobId() + "_" + jobCard.getVehicleRegNo() +
		// ".pdf";
		//// HttpHeaders headers = new HttpHeaders();
		//// headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +
		// filename);
		//// return
		// ResponseEntity.ok().headers(headers).contentLength(resource.contentLength())
		//// .contentType(MediaType.APPLICATION_PDF).body(resource);
		//// } catch (Exception e) {
		//// throw new Exception(e);
		//// }
		//
		// ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		// PdfWriter pdfWriter = new PdfWriter(outputStream);
		// PdfDocument pdfDocument = new PdfDocument(pdfWriter);
		// Document document = new Document(pdfDocument);
		//
		// document.add(table);
		// document.add(singleColumnTable);
		// document.add(doubleColumnTable);
		// document.add(table1);
		// document.add(table2);
		// if (table3 != null) {
		// document.add(table3);
		// }
		// document.close();
		// pdfDocument.close();
		// pdfWriter.close();
		// outputStream.close();
		//
		// ByteArrayResource resource = new
		// ByteArrayResource(outputStream.toByteArray());
		// String filename = "JobCard_" + jobCard.getJobId() + "_" +
		// jobCard.getVehicleRegNo() + ".pdf";
		ByteArrayResource resource = new ByteArrayResource(null);
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=");
		return ResponseEntity.ok().headers(headers).contentLength(resource.contentLength())
				.contentType(MediaType.APPLICATION_PDF).body(resource);
	}

	// private Paragraph createCellWithFixedSpace(String... texts) {
	// // TODO Auto-generated method stub
	// String regex = "Job Card No: |Owner: |Address: |Contact No: |Driver: |Vehicle
	// Reg. No: |Vehicle Model: |Technician Name: |Type of Vehicle: |K.M: |Vehicle
	// Out Date: |Date: |Email: ";
	// Paragraph paragraph = new Paragraph();
	// for (String text : texts) {
	// if (text.equals("\n")) {
	// paragraph.add(text);
	// } else if (text.matches(regex)) {
	// paragraph.add(new Text(text).setBold());
	// } else {
	// paragraph.add(new Text(text).setTextAlignment(TextAlignment.RIGHT));
	// }
	//
	// }
	// return paragraph;
	//
	// }
	//
	// private void setMinWidth(Table table, int rowIndex, int columnIndex, float
	// minWidth) {
	// if (rowIndex < table.getNumberOfRows() && columnIndex <
	// table.getNumberOfColumns()) {
	// table.getCell(rowIndex, columnIndex).setMinWidth(minWidth);
	// }
	// }
	//
	// private void updateVechicleItems(Table table1, String items) {
	// if (items != null) {
	// if (items.equals("OK")) {
	// table1.addCell("OK");
	// table1.addCell("");
	// } else {
	// table1.addCell("");
	// table1.addCell("NOT OK");
	// }
	// } else {
	// table1.addCell("");
	// table1.addCell("");
	// }
	// }

	// private String stringNullCheck(Object str) {
	// if (str == null)
	// return "";
	// return String.valueOf(str);
	// }

	// private String createDateString(LocalDateTime date) {
	// if (date != null) {
	// return date.getDayOfMonth() + "/" + date.getMonthValue() + "/" +
	// date.getYear();
	// }
	// return "";
	// }

	// private String removeJobSparesBracketFieldsAndNullCheck(Object str) {
	// if (str == null)
	// return "";
	// return String.valueOf(str).replaceAll("\s*\(.*?\)\s*", "").trim();
	// }

	private void sendNotifications(String title, String body) {
		// EmailDetails emailDetails =
		// EmailDetails.builder().msgBody(title).subject(title).build();
		// emailService.sendTableMail(emailDetails, emailRecepients, body);

	}

	// public ResponseEntity<?> generateBillPdf(String id) throws Exception {
	// JobCard jobCard = jobCardRepository.findById(id).orElse(null);
	// JobSpares jobSpares = jobSparesRepository.findById(id).orElse(null);
	//
	// if (jobCard == null) {
	// throw new Exception("JobCard not found for id " + id);
	// }
	//
	// if (jobSpares == null) {
	// throw new Exception("JobSpares not found for id " + id);
	// }
	//
	// // Create PDF document and writer
	// ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	// PdfWriter pdfWriter = new PdfWriter(outputStream);
	// PdfDocument pdfDocument = new PdfDocument(pdfWriter);
	// Document document = new Document(pdfDocument);
	//
	// Table table = new Table(UnitValue.createPercentArray(new float[] { 70, 30
	// }));
	// table.setWidth(UnitValue.createPercentValue(100));
	//
	// Paragraph businessDetails = new Paragraph().add(new Text("JAI MARUTHI AUTO
	// CARE\n").setBold().setFontSize(20))
	// .add(new Text("#188-B, Mettukadai, Kathirampatti, Erode - 638
	// 107\n").setFontSize(10))
	// .add(new Text("E-mail: jmacerode@gmail.com | Cell: 63801 68789, 88256
	// 06390").setFontSize(10));
	//
	// // Add the business details Paragraph to the first cell of the table
	// table.addCell(new
	// Cell().add(businessDetails).setVerticalAlignment(VerticalAlignment.MIDDLE)
	// .setHorizontalAlignment(HorizontalAlignment.LEFT));
	//
	// Image image = new Image(ImageDataFactory.create("classpath:jm_logo_1.jpeg"));
	// image.setMaxHeight(120);
	// image.setMaxWidth(150);
	// table.addCell(
	// new
	// Paragraph("").add(image).setVerticalAlignment(VerticalAlignment.MIDDLE).setKeepTogether(true));
	// document.add(table);
	//
	// // Header Section - Add business info
	// Table headerTable = new Table(UnitValue.createPercentArray(new float[] { 100
	// }));
	// headerTable.setWidth(UnitValue.createPercentValue(100));
	//
	// headerTable.addCell(new Cell()
	// .add(new
	// Paragraph("INVOICE").setBold().setFontSize(14).setTextAlignment(TextAlignment.CENTER)));
	// document.add(headerTable);
	//
	// Table customerInfoTable = new Table(UnitValue.createPercentArray(new float[]
	// { 40, 30, 30 }));
	// customerInfoTable.setWidth(UnitValue.createPercentValue(100));
	// customerInfoTable.addCell(new Cell().add(new Paragraph("Customer Name: " +
	// jobCard.getOwnerName())
	// .setFontSize(10).setTextAlignment(TextAlignment.LEFT)));
	// customerInfoTable.addCell(new Cell().add(new Paragraph("Ph No. " +
	// jobCard.getOwnerPhoneNumber())
	// .setFontSize(10).setTextAlignment(TextAlignment.LEFT)));
	//
	// customerInfoTable.addCell(new Cell().add(new Paragraph("Date: " +
	// createDateString(LocalDateTime.now()))
	// .setFontSize(10).setTextAlignment(TextAlignment.LEFT)));
	//
	// document.add(customerInfoTable);
	//
	// // Add Invoice Info Section
	// Table invoiceInfoTable = new Table(UnitValue.createPercentArray(new float[] {
	// 18, 32, 25, 25 }));
	// invoiceInfoTable.setWidth(UnitValue.createPercentValue(100));
	// invoiceInfoTable.addCell(new Cell().add(new Paragraph("Invoice No: " +
	// jobCard.getInvoiceId()).setFontSize(10)
	// .setTextAlignment(TextAlignment.LEFT)));
	// invoiceInfoTable.addCell(new Cell().add(new Paragraph("V. Name: " +
	// jobCard.getVehicleName()).setFontSize(10)
	// .setTextAlignment(TextAlignment.LEFT)));
	// invoiceInfoTable.addCell(new Cell().add(new Paragraph("V. No: " +
	// jobCard.getVehicleRegNo()).setFontSize(10)
	// .setTextAlignment(TextAlignment.LEFT)));
	// invoiceInfoTable.addCell(new Cell().add(new Paragraph("V. KMs: " +
	// jobCard.getKiloMeters())).setFontSize(10)
	// .setTextAlignment(TextAlignment.LEFT));
	// document.add(invoiceInfoTable);
	//
	// // Add Job No and Customer's Order Section
	// Table orderInfoTable = new Table(UnitValue.createPercentArray(new float[] {
	// 50, 50 }));
	// orderInfoTable.setWidth(UnitValue.createPercentValue(100));
	// orderInfoTable.addCell(new Cell()
	// .add(new Paragraph("Customer’s Order No & Date: " +
	// createDateString(jobCard.getJobCreationDate()))
	// .setFontSize(10).setTextAlignment(TextAlignment.LEFT)));
	// orderInfoTable.addCell(new Cell().add(
	// new Paragraph("Job No: " +
	// jobCard.getJobId()).setFontSize(10).setTextAlignment(TextAlignment.LEFT)));
	//
	// document.add(orderInfoTable);
	//
	// // Create table for Spares and Labour (with proper headers)
	// Table itemTable = new Table(UnitValue.createPercentArray(new float[] { 10,
	// 40, 10, 20, 20 }));
	// itemTable.setWidth(UnitValue.createPercentValue(100));
	// itemTable.addCell(new Cell().add(new
	// Paragraph("S.No").setTextAlignment(TextAlignment.CENTER).setBold()));
	// itemTable
	// .addCell(new Cell().add(new
	// Paragraph("Particulars").setTextAlignment(TextAlignment.CENTER).setBold()));
	// itemTable.addCell(new Cell().add(new
	// Paragraph("Qty").setTextAlignment(TextAlignment.CENTER).setBold()));
	// itemTable.addCell(new Cell().add(new
	// Paragraph("Rate/Unit").setTextAlignment(TextAlignment.CENTER).setBold()));
	// itemTable.addCell(new Cell().add(new
	// Paragraph("Amount").setTextAlignment(TextAlignment.CENTER).setBold()));
	//
	// // Add Spares and Labor details
	// int itemIndex = 1;
	// if (jobSpares != null && jobSpares.getJobSparesInfo() != null) {
	// for (JobSparesInfo sparesInfo : jobSpares.getJobSparesInfo()) {
	// String units = sparesInfo.getUnits() != null ? sparesInfo.getUnits() : "";
	//
	// itemTable.addCell(new Cell()
	// .add(new
	// Paragraph(String.valueOf(itemIndex++)).setTextAlignment(TextAlignment.CENTER)));
	// itemTable.addCell(new Cell()
	// .add(new
	// Paragraph(removeJobSparesBracketFieldsAndNullCheck(sparesInfo.getSparesAndLabour()))));
	// itemTable.addCell(new Cell().add(
	// new Paragraph(sparesInfo.getQty().toString() +
	// units).setTextAlignment(TextAlignment.RIGHT)));
	// itemTable.addCell(new Cell()
	// .add(new
	// Paragraph(sparesInfo.getRate().toString()).setTextAlignment(TextAlignment.RIGHT)));
	// itemTable.addCell(new Cell()
	// .add(new
	// Paragraph(sparesInfo.getAmount().toString()).setTextAlignment(TextAlignment.RIGHT)));
	// }
	// }
	//
	// if (jobSpares != null && jobSpares.getJobConsumablesInfo() != null) {
	// for (JobSparesInfo sparesInfo : jobSpares.getJobConsumablesInfo()) {
	// String units = sparesInfo.getUnits() != null ? sparesInfo.getUnits() : "";
	//
	// itemTable.addCell(new Cell()
	// .add(new
	// Paragraph(String.valueOf(itemIndex++)).setTextAlignment(TextAlignment.CENTER)));
	// itemTable.addCell(new Cell()
	// .add(new
	// Paragraph(removeJobSparesBracketFieldsAndNullCheck(sparesInfo.getSparesAndLabour()))));
	// itemTable.addCell(new Cell().add(
	// new Paragraph("").setTextAlignment(TextAlignment.RIGHT)));
	// itemTable.addCell(new Cell()
	// .add(new Paragraph("").setTextAlignment(TextAlignment.RIGHT)));
	// itemTable.addCell(new Cell()
	// .add(new
	// Paragraph(sparesInfo.getAmount().toString()).setTextAlignment(TextAlignment.RIGHT)));
	// }
	// }
	//
	// if (jobSpares != null && jobSpares.getJobLaborInfo() != null) {
	// for (JobSparesInfo sparesInfo : jobSpares.getJobLaborInfo()) {
	// itemTable.addCell(new Cell()
	// .add(new
	// Paragraph(String.valueOf(itemIndex++)).setTextAlignment(TextAlignment.CENTER)));
	// itemTable.addCell(new Cell()
	// .add(new
	// Paragraph(removeJobSparesBracketFieldsAndNullCheck(sparesInfo.getSparesAndLabour()))));
	// itemTable.addCell(new Cell()
	// .add(new
	// Paragraph(sparesInfo.getQty().toString()).setTextAlignment(TextAlignment.RIGHT)));
	// itemTable.addCell(new Cell()
	// .add(new
	// Paragraph(sparesInfo.getRate().toString()).setTextAlignment(TextAlignment.RIGHT)));
	// itemTable.addCell(new Cell()
	// .add(new
	// Paragraph(sparesInfo.getAmount().toString()).setTextAlignment(TextAlignment.RIGHT)));
	// }
	// }
	//
	// document.add(itemTable);
	//
	// // Add Total section for Spares, Labour, and Grand Total
	// Table totalTable = new Table(UnitValue.createPercentArray(new float[] { 80,
	// 20 }));
	// totalTable.setWidth(UnitValue.createPercentValue(100));
	// totalTable
	// .addCell(new Cell().add(new Paragraph("Grand
	// Total").setBold().setTextAlignment(TextAlignment.RIGHT)));
	// totalTable.addCell(new Cell()
	// .add(new
	// Paragraph(stringNullCheck(jobSpares.getGrandTotal())).setTextAlignment(TextAlignment.RIGHT)));
	// document.add(totalTable);
	//
	// // Add Note Section
	// Table noteTable = new Table(UnitValue.createPercentArray(new float[] { 100
	// }));
	// noteTable.setWidth(UnitValue.createPercentValue(100));
	// noteTable.addCell(new Cell().add(new Paragraph(
	// "Note: Goods once sold cannot be taken back. Warranty of the Components are
	// applicable only subjected to manufacturing defects. Not for improper (or)
	// wear condition of the components.")
	// .setFontSize(8).setTextAlignment(TextAlignment.LEFT)));
	// document.add(noteTable);
	//
	// Table table1 = new Table(UnitValue.createPercentArray(new float[] { 65, 35
	// }));
	// table1.setWidth(UnitValue.createPercentValue(100));
	//
	// Paragraph businessDetails1 = new Paragraph().add(new Text(
	// "Received the above goods in good condition and we have agreed to the price
	// and other terms shows above.\n")
	// .setFontSize(8)) // Larger and bold font for the name
	// .add(new Text("\n").setFontSize(10)) // Smaller font for address
	// .add(new
	// Text("Signature").setFontSize(10).setHorizontalAlignment(HorizontalAlignment.CENTER));
	//
	// table1.addCell(new
	// Cell().add(businessDetails1).setVerticalAlignment(VerticalAlignment.MIDDLE)
	// .setHorizontalAlignment(HorizontalAlignment.LEFT));
	//
	// Paragraph businessDetails2 = new Paragraph()
	// .add(new Text("For JAI MARUTHI AUTO CARE\n").setBold().setFontSize(10)) //
	// Larger and bold font for the
	// // name
	// .add(new Text("\n").setFontSize(10)) // Smaller font for address
	// .add(new Text("Authorized Signature").setFontSize(10)
	// .setHorizontalAlignment(HorizontalAlignment.CENTER)); // Smaller font for
	// email and phone
	//
	// table1.addCell(new
	// Cell().add(businessDetails2).setVerticalAlignment(VerticalAlignment.MIDDLE)
	// .setHorizontalAlignment(HorizontalAlignment.LEFT));
	//
	// document.add(table1);
	//
	// // Close Document
	// document.close();
	// pdfDocument.close();
	// pdfWriter.close();
	// outputStream.close();
	//
	// ByteArrayResource resource = new
	// ByteArrayResource(outputStream.toByteArray());
	// String filename = "Bill_" + jobCard.getJobId() + "_" +
	// jobCard.getVehicleRegNo() + ".pdf";
	// HttpHeaders headers = new HttpHeaders();
	// headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +
	// filename);
	// return
	// ResponseEntity.ok().headers(headers).contentLength(resource.contentLength())
	// .contentType(MediaType.APPLICATION_PDF).body(resource);
	// }

	// void addEmptyRow(Table table) {
	//
	// table.addCell(new Cell().setMinHeight(rowHeight)
	// .add(new
	// Paragraph("").setFontSize(10).setTextAlignment(TextAlignment.CENTER)));
	// table.addCell(new Cell().setMinHeight(rowHeight)
	// .add(new
	// Paragraph("").setFontSize(10).setTextAlignment(TextAlignment.LEFT)));
	// table.addCell(new Cell().setMinHeight(rowHeight)
	// .add(new
	// Paragraph("").setFontSize(10).setTextAlignment(TextAlignment.RIGHT)));
	// table.addCell(new Cell().setMinHeight(rowHeight)
	// .add(new
	// Paragraph("").setFontSize(10).setTextAlignment(TextAlignment.RIGHT)));
	// table.addCell(new Cell().setMinHeight(rowHeight)
	// .add(new
	// Paragraph("").setFontSize(10).setTextAlignment(TextAlignment.RIGHT)));
	// }

	public JobVehiclePhotos saveZipToMongo(MultipartFile zipFile, String id) throws IOException {

		JobVehiclePhotos photos = JobVehiclePhotos.builder().id(id).name(zipFile.getOriginalFilename())
				.content(zipFile.getBytes()).contentType(zipFile.getContentType()).build();
		return jobVehiclePhotosRepository.save(photos);

	}

	public JobVehiclePhotos getZipPhotos(String id) {
		// TODO Auto-generated method stub
		return jobVehiclePhotosRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Photos not found for id " + id));
	}

	@Scheduled(cron = "0 0 2 * * ?") // Runs daily at 2 AM
	public void deleteOldPhotos() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -30);
		Date cutoff = calendar.getTime();

		jobVehiclePhotosRepository.deleteByCreatedAtBefore(cutoff);
	}

	public ResponseEntity<ByteArrayResource> invoicePdf(String id) throws Exception {

		JobCard jobCard = jobCardRepository.findById(id).orElse(null);
		JobSpares jobSpares = jobSparesRepository.findById(id).orElse(null);
		Invoice invoice = invoiceRepository.findById(jobCard.getInvoiceObjId()).orElse(null);

		if (jobCard == null) {
			throw new Exception("JobCard not found for id " + id);
		}

		if (jobSpares == null) {
			throw new Exception("JobSpares not found for id " + id);
		}

		if (invoice == null) {
			throw new Exception("Invoice not found for id " + id);
		}

		String paymentMode = "";
		// for (PaymentSplit payment : invoice.getPaymentSplitList()) {
		// if (!payment.getPaymentMode().equals("CREDIT")) {
		// paymentMode = paymentMode + payment.getPaymentMode() + " ";
		// }
		// }
		paymentMode = invoice.getPaymentSplitList().stream()
				.filter(payment -> !payment.getPaymentMode().equals("CREDIT")).map(s -> s.getPaymentMode())
				.collect(Collectors.joining(", "));
		// NEED to work whether to consider CREDIT list also. Also consider unique
		// value. May be use Set<String>
		// if (paymentMode.equals("")) {
		// paymentMode = invoice.getCreditPaymentList().get(0).getPaymentMode();
		// }
		if (paymentMode.equals("")) {
			paymentMode = invoice.getCreditPaymentList().stream()
					.filter(payment -> !payment.getPaymentMode().equals("CREDIT")).map(s -> s.getPaymentMode())
					.collect(Collectors.joining(", "));
		}

		Map<String, Object> data = new HashMap<>();
		data.put("vehNo", jobCard.getVehicleRegNo());
		data.put("ownerName", jobCard.getOwnerName());
		data.put("kms", jobCard.getKiloMeters());
		data.put("invoiceNo", invoice.getInvoiceId());
		data.put("date", invoice.getBillCloseDate());
		data.put("mode", paymentMode);
		data.put("nextFreeCheckKms", jobCard.getKiloMeters() != null ? jobCard.getKiloMeters() + 1500 : null);
		data.put("vehicle", jobCard.getVehicleName());
		data.put("nextServiceKms", jobCard.getKiloMeters() != null ? jobCard.getKiloMeters() + 3000 : null);

		data.put("totalTaxAmt", jobSpares.getGrandTotalWithGST().subtract(jobSpares.getGrandTotal()));
		data.put("roundOff", "");
		data.put("netAmount", invoice.getGrandTotal());
		data.put("amountInWords", NumberToWordsConverter.convert(invoice.getGrandTotal()));

		Map<BigDecimal, BigDecimal> taxMap = new HashMap<>();
		int count = 1;
		BigDecimal totalQty = BigDecimal.ZERO;

		List<Map<String, Object>> productList = new ArrayList<>();
		for (int i = 0; i < jobSpares.getJobServiceInfo().size(); i++) {
			JobSparesInfo jobSparesInfo = jobSpares.getJobServiceInfo().get(i);
			ServiceInventory service = serviceInventoryRepository.findById(jobSparesInfo.getSparesId()).orElse(null);

			// Update the taxMap
			BigDecimal gstPercentage = jobSparesInfo.getGstPercentage();
			BigDecimal amount = jobSparesInfo.getAmount();

			// Add amount to the taxMap for the corresponding gstPercentage
			taxMap.put(gstPercentage, taxMap.getOrDefault(gstPercentage, BigDecimal.ZERO).add(amount));
			totalQty = totalQty.add(jobSparesInfo.getQty());
			productList.add(Map.of("sno", count++, "name", jobSparesInfo.getSparesAndLabour(), "hsnCode",
					service.getHsnCode() != null ? service.getHsnCode() : "", "qty", jobSparesInfo.getQty(), "rate",
					jobSparesInfo.getRate(), "gst", gstPercentage.add(gstPercentage) + "%", "discount",
					jobSparesInfo.getDiscount() != null ? jobSparesInfo.getDiscount() : "", "amount", amount));

		}
		for (int i = 0; i < jobSpares.getJobSparesInfo().size(); i++) {
			JobSparesInfo jobSparesInfo = jobSpares.getJobServiceInfo().get(i);
			ServiceInventory service = serviceInventoryRepository.findById(jobSparesInfo.getSparesId()).orElse(null);
			// Update the taxMap
			BigDecimal gstPercentage = jobSparesInfo.getGstPercentage();
			BigDecimal amount = jobSparesInfo.getAmount();

			// Add amount to the taxMap for the corresponding gstPercentage
			taxMap.put(gstPercentage, taxMap.getOrDefault(gstPercentage, BigDecimal.ZERO).add(amount));
			totalQty = totalQty.add(jobSparesInfo.getQty());
			productList.add(Map.of("sno", count++, "name", jobSparesInfo.getSparesAndLabour(), "hsnCode",
					service.getHsnCode() != null ? service.getHsnCode() : "", "qty", jobSparesInfo.getQty(), "rate",
					jobSparesInfo.getRate(), "gst", gstPercentage.add(gstPercentage) + "%", "discount",
					jobSparesInfo.getDiscount() != null ? jobSparesInfo.getDiscount() : "", "amount", amount));

		}

		while (count < 15) {
			productList.add(Map.of("sno", "", "name", "", "hsnCode", "\t", "qty", "\t", "rate", "\t", "gst", "\t",
					"discount", "\t", "amount", "\t"));
			count++;
		}

		if (count > 15 && count < 40) {
			data.put("pagebreak", true);
		}

		productList.add(Map.of("sno", "", "name", "E & OE", "hsnCode", "", "qty", totalQty, "rate", "", "gst", "",
				"discount", "", "amount", jobSpares.getGrandTotal()));

		data.put("products", productList);

		List<Map<String, Object>> taxList = new ArrayList<>();

		taxMap.forEach((k, v) -> {
			BigDecimal gstAmount = v.multiply(k).divide(BigDecimal.valueOf(100));
			taxList.add(Map.of("taxableValue", v, "cgstPercent", k, "cgstAmt", gstAmount, "sgstPercent", k, "sgstAmt",
					gstAmount, "netPercent", k.add(k) + "%", "amount", gstAmount.add(gstAmount)));
		});
		data.put("taxDetails", taxList);

		ByteArrayResource resource = pdfUtils.generateHTMLPdf(data, "invoice");

		String filename = "Invoice_" + invoice.getInvoiceId() + ".pdf";

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

		return ResponseEntity.ok().headers(headers).contentLength(resource.contentLength())
				.contentType(MediaType.APPLICATION_PDF).body(resource);
	}

	public ResponseEntity<ByteArrayResource> estimatePdf(String id) throws Exception {

		JobCard jobCard = jobCardRepository.findById(id).orElse(null);
		JobSpares jobSpares = jobSparesRepository.findById(id).orElse(null);
		Estimate estimate = estimateRepository.findById(jobCard.getEstimateObjId()).orElse(null);

		if (jobCard == null) {
			throw new Exception("JobCard not found for id " + id);
		}

		if (jobSpares == null) {
			throw new Exception("JobSpares not found for id " + id);
		}

		if (estimate == null) {
			throw new Exception("Estimate not found for id " + id);
		}

		String paymentMode = "";
		// for (PaymentSplit payment : estimate.getPaymentSplitList()) {
		// if (!payment.getPaymentMode().equals("CREDIT")) {
		// paymentMode = paymentMode + payment.getPaymentMode() + " ";
		// }
		// }
		paymentMode = estimate.getPaymentSplitList().stream()
				.filter(payment -> !payment.getPaymentMode().equals("CREDIT")).map(s -> s.getPaymentMode())
				.collect(Collectors.joining(", "));
		// NEED to work whether to consider CREDIT list also. Also consider unique
		// value. May be use Set<String>
		// if (paymentMode.equals("")) {
		// paymentMode = estimate.getCreditPaymentList().get(0).getPaymentMode();
		// }
		if (paymentMode.equals("")) {
			paymentMode = estimate.getCreditPaymentList().stream()
					.filter(payment -> !payment.getPaymentMode().equals("CREDIT")).map(s -> s.getPaymentMode())
					.collect(Collectors.joining(", "));
		}

		Map<String, Object> data = new HashMap<>();
		data.put("vehNo", jobCard.getVehicleRegNo());
		data.put("ownerName", jobCard.getOwnerName());
		data.put("kms", jobCard.getKiloMeters());
		data.put("estimateNo", estimate.getEstimateId());
		data.put("date", estimate.getBillCloseDate());
		data.put("mode", paymentMode);
		data.put("nextFreeCheckKms", jobCard.getKiloMeters() != null ? jobCard.getKiloMeters() + 1500 : null);
		data.put("vehicle", jobCard.getVehicleName());
		data.put("nextServiceKms", jobCard.getKiloMeters() != null ? jobCard.getKiloMeters() + 3000 : null);

		data.put("netAmount", estimate.getGrandTotal());
		data.put("amountInWords", NumberToWordsConverter.convert(estimate.getGrandTotal()));

		int count = 1;
		BigDecimal totalQty = BigDecimal.ZERO;

		List<Map<String, Object>> productList = new ArrayList<>();
		for (int i = 0; i < jobSpares.getJobServiceInfo().size(); i++) {
			JobSparesInfo jobSparesInfo = jobSpares.getJobServiceInfo().get(i);
			ServiceInventory service = serviceInventoryRepository.findById(jobSparesInfo.getSparesId()).orElse(null);

			BigDecimal amount = jobSparesInfo.getAmount();

			totalQty = totalQty.add(jobSparesInfo.getQty());
			productList.add(Map.of("sno", count++, "name", jobSparesInfo.getSparesAndLabour(), "hsnCode",
					service.getHsnCode() != null ? service.getHsnCode() : "", "qty", jobSparesInfo.getQty(), "rate",
					jobSparesInfo.getRate(), "discount",
					jobSparesInfo.getDiscount() != null ? jobSparesInfo.getDiscount() : "", "amount", amount));

		}
		for (int i = 0; i < jobSpares.getJobSparesInfo().size(); i++) {
			JobSparesInfo jobSparesInfo = jobSpares.getJobServiceInfo().get(i);
			ServiceInventory service = serviceInventoryRepository.findById(jobSparesInfo.getSparesId()).orElse(null);

			BigDecimal amount = jobSparesInfo.getAmount();

			totalQty = totalQty.add(jobSparesInfo.getQty());
			productList.add(Map.of("sno", count++, "name", jobSparesInfo.getSparesAndLabour(), "hsnCode",
					service.getHsnCode() != null ? service.getHsnCode() : "", "qty", jobSparesInfo.getQty(), "rate",
					jobSparesInfo.getRate(), "discount",
					jobSparesInfo.getDiscount() != null ? jobSparesInfo.getDiscount() : "", "amount", amount));

		}

		while (count < 15) {
			productList.add(Map.of("sno", "", "name", "", "hsnCode", "\t", "qty", "\t", "rate", "\t", "discount", "\t",
					"amount", "\t"));
			count++;
		}

		if (count > 15 && count < 40) {
			data.put("pagebreak", true);
		}

		productList.add(Map.of("sno", "", "name", "E & OE", "hsnCode", "", "qty", totalQty, "rate", "", "discount", "",
				"amount", jobSpares.getGrandTotal()));

		data.put("products", productList);

		ByteArrayResource resource = pdfUtils.generateHTMLPdf(data, "estimate");

		String filename = "Estimate_" + estimate.getEstimateId() + ".pdf";

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

		return ResponseEntity.ok().headers(headers).contentLength(resource.contentLength())
				.contentType(MediaType.APPLICATION_PDF).body(resource);
	}

	public ResponseEntity<?> downloadBillPdf(String id) throws Exception {
		JobSpares jobSpares = jobSparesRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("JobCard not found for id " + id));

		if (jobSpares.getInvoiceObjId() != null) {
			return invoicePdf(id);
		} else if (jobSpares.getEstimateObjId() != null) {
			return estimatePdf(id);
		} else {
			throw new Exception("Bill must be generated first");
		}

	}

	public void recalculateAndUpdateEstimate(JobSpares jobSpares) throws Exception {
		Estimate estimate = estimateRepository.findById(jobSpares.getEstimateObjId())
				.orElseThrow(() -> new RuntimeException("Estimate ID not found"));

		BigDecimal newGrandTotal = Optional.ofNullable(jobSpares.getGrandTotal()).orElse(BigDecimal.ZERO);
		BigDecimal oldGrandTotal = Optional.ofNullable(estimate.getGrandTotal()).orElse(BigDecimal.ZERO);
		BigDecimal difference = newGrandTotal.subtract(oldGrandTotal);

		// If no change, return
		if (difference.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		// Set new grand total
		estimate.setGrandTotal(newGrandTotal);

		// Calculate total amount paid so far
		BigDecimal totalPaid = estimate.getPaymentSplitList().stream()
				.filter(p -> !"CREDIT".equalsIgnoreCase(p.getPaymentMode()))
				.map(p -> Optional.ofNullable(p.getPaymentAmount()).orElse(BigDecimal.ZERO))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal totalCreditPaid = estimate.getCreditPaymentList().stream()
				.map(c -> Optional.ofNullable(c.getAmount()).orElse(BigDecimal.ZERO))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal totalActualPaid = totalPaid.add(totalCreditPaid);

		// If new total is MORE than old
		if (difference.compareTo(BigDecimal.ZERO) > 0) {
			// Increase pending amount
			BigDecimal newPending = estimate.getPendingAmount().add(difference);
			estimate.setPendingAmount(newPending);

			// Mark as credit
			estimate.setCreditFlag(true);
			estimate.setCreditSettledFlag(false);

			// This is a bill reopen scenario. The flag is ADD because there is payments
			// with credit mode are ignored for any flags.
			// At UI side this extra amount goes as credit with add flag, but user will
			// change it to CASH or UPI etc and saved with ADD flag to include as new
			// payment
			PaymentSplit split = PaymentSplit.builder().paymentAmount(newPending).paymentMode("CREDIT").flag("ADD")
					.build();
			List<PaymentSplit> splitList = estimate.getPaymentSplitList();
			if (splitList != null && !splitList.isEmpty()) {
				splitList.add(split);
			} else {
				splitList = new ArrayList<>();
				splitList.add(split);
			}

		} else {
			// Overpayment: try to reduce credit payments first
			BigDecimal overpaid = difference.abs(); // positive amount to reduce

			// Remove from creditPaymentList (latest first)
			List<CreditPayment> creditPayments = estimate.getCreditPaymentList();
			creditPayments.sort(Comparator.comparing(CreditPayment::getCreditDate).reversed());

			Iterator<CreditPayment> cpIterator = creditPayments.iterator();
			while (cpIterator.hasNext() && overpaid.compareTo(BigDecimal.ZERO) > 0) {
				CreditPayment cp = cpIterator.next();
				BigDecimal amt = cp.getAmount();

				if (overpaid.compareTo(amt) >= 0) {
					overpaid = overpaid.subtract(amt);
					cpIterator.remove(); // remove entire payment

					// inform payment service
					paymentsService.deleteById(cp.getPaymentId());
				} else {
					cp.setAmount(amt.subtract(overpaid));
					overpaid = BigDecimal.ZERO;

					// inform payment service
					Payments payments = paymentsService.findById(cp.getPaymentId());
					payments.setPaymentAmount(cp.getAmount());
					payments.setPaymentMode(cp.getPaymentMode());
					paymentsService.save(payments);
				}
			}

			// Remove from paymentSplitList (excluding CREDIT)
			List<PaymentSplit> splits = estimate.getPaymentSplitList();
			splits.sort(Comparator.comparing(PaymentSplit::getPaymentAmount).reversed());

			Iterator<PaymentSplit> psIterator = splits.iterator();
			while (psIterator.hasNext() && overpaid.compareTo(BigDecimal.ZERO) > 0) {
				PaymentSplit ps = psIterator.next();
				if ("CREDIT".equalsIgnoreCase(ps.getPaymentMode()))
					continue;

				BigDecimal amt = ps.getPaymentAmount();
				if (overpaid.compareTo(amt) >= 0) {
					overpaid = overpaid.subtract(amt);
					psIterator.remove();

					// inform payment service
					paymentsService.deleteById(ps.getPaymentId());
				} else {
					ps.setPaymentAmount(amt.subtract(overpaid));
					overpaid = BigDecimal.ZERO;

					// inform payment service
					Payments payments = paymentsService.findById(ps.getPaymentId());
					payments.setPaymentAmount(ps.getPaymentAmount());
					payments.setPaymentMode(ps.getPaymentMode());
					paymentsService.save(payments);
				}
			}

			// Recalculate total paid after adjustment
			totalPaid = splits.stream().filter(p -> !"CREDIT".equalsIgnoreCase(p.getPaymentMode()))
					.map(p -> Optional.ofNullable(p.getPaymentAmount()).orElse(BigDecimal.ZERO))
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			totalCreditPaid = creditPayments.stream()
					.map(p -> Optional.ofNullable(p.getAmount()).orElse(BigDecimal.ZERO))
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			totalActualPaid = totalPaid.add(totalCreditPaid);

			// New pending
			BigDecimal newPending = newGrandTotal.subtract(totalActualPaid);
			estimate.setPendingAmount(newPending.max(BigDecimal.ZERO)); // no negative pending

			// Credit flags
			boolean hasCredit = splits.stream().anyMatch(p -> "CREDIT".equalsIgnoreCase(p.getPaymentMode()));

			estimate.setCreditFlag(hasCredit);
			estimate.setCreditSettledFlag(hasCredit && newPending.compareTo(BigDecimal.ZERO) == 0);
		}

		// Save back the updated estimate (pseudo code - use repo)
		estimateRepository.save(estimate);
	}

	public void recalculateAndUpdateInvoice(JobSpares jobSpares) throws Exception {
		Invoice invoice = invoiceRepository.findById(jobSpares.getInvoiceObjId())
				.orElseThrow(() -> new RuntimeException("Invoice ID not found"));

		BigDecimal newGrandTotal = Optional.ofNullable(jobSpares.getGrandTotal()).orElse(BigDecimal.ZERO);
		BigDecimal oldGrandTotal = Optional.ofNullable(invoice.getGrandTotal()).orElse(BigDecimal.ZERO);
		BigDecimal difference = newGrandTotal.subtract(oldGrandTotal);

		// If no change, return
		if (difference.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		// Set new grand total
		invoice.setGrandTotal(newGrandTotal);

		// Calculate total amount paid so far
		BigDecimal totalPaid = invoice.getPaymentSplitList().stream()
				.filter(p -> !"CREDIT".equalsIgnoreCase(p.getPaymentMode()))
				.map(p -> Optional.ofNullable(p.getPaymentAmount()).orElse(BigDecimal.ZERO))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal totalCreditPaid = invoice.getCreditPaymentList().stream()
				.map(c -> Optional.ofNullable(c.getAmount()).orElse(BigDecimal.ZERO))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal totalActualPaid = totalPaid.add(totalCreditPaid);

		// If new total is MORE than old
		if (difference.compareTo(BigDecimal.ZERO) > 0) {
			// Increase pending amount
			BigDecimal newPending = invoice.getPendingAmount().add(difference);
			invoice.setPendingAmount(newPending);

			// Mark as credit
			invoice.setCreditFlag(true);
			invoice.setCreditSettledFlag(false);
			// This is a bill reopen scenario. The flag is ADD because there is payments
			// with credit mode are ignored for any flags.
			// At UI side this extra amount goes as credit with add flag, but user will
			// change it to CASH or UPI etc and saved with ADD flag to include as new
			// payment
			PaymentSplit split = PaymentSplit.builder().paymentAmount(newPending).paymentMode("CREDIT").flag("ADD")
					.build();
			List<PaymentSplit> splitList = invoice.getPaymentSplitList();
			if (splitList != null && !splitList.isEmpty()) {
				splitList.add(split);
			} else {
				splitList = new ArrayList<>();
				splitList.add(split);
			}

		} else {
			// Overpayment: try to reduce credit payments first
			BigDecimal overpaid = difference.abs(); // positive amount to reduce

			// Remove from creditPaymentList (latest first)
			List<CreditPayment> creditPayments = invoice.getCreditPaymentList();
			creditPayments.sort(Comparator.comparing(CreditPayment::getCreditDate).reversed());

			Iterator<CreditPayment> cpIterator = creditPayments.iterator();
			while (cpIterator.hasNext() && overpaid.compareTo(BigDecimal.ZERO) > 0) {
				CreditPayment cp = cpIterator.next();
				BigDecimal amt = cp.getAmount();

				if (overpaid.compareTo(amt) >= 0) {
					overpaid = overpaid.subtract(amt);
					cpIterator.remove(); // remove entire payment

					// inform payment service
					paymentsService.deleteById(cp.getPaymentId());
				} else {
					cp.setAmount(amt.subtract(overpaid));
					overpaid = BigDecimal.ZERO;

					// inform payment service
					Payments payments = paymentsService.findById(cp.getPaymentId());
					payments.setPaymentAmount(cp.getAmount());
					payments.setPaymentMode(cp.getPaymentMode());
					paymentsService.save(payments);
				}
			}

			// Remove from paymentSplitList (excluding CREDIT)
			List<PaymentSplit> splits = invoice.getPaymentSplitList();
			splits.sort(Comparator.comparing(PaymentSplit::getPaymentAmount).reversed());

			Iterator<PaymentSplit> psIterator = splits.iterator();
			while (psIterator.hasNext() && overpaid.compareTo(BigDecimal.ZERO) > 0) {
				PaymentSplit ps = psIterator.next();
				if ("CREDIT".equalsIgnoreCase(ps.getPaymentMode()))
					continue;

				BigDecimal amt = ps.getPaymentAmount();
				if (overpaid.compareTo(amt) >= 0) {
					overpaid = overpaid.subtract(amt);
					psIterator.remove();

					// inform payment service
					paymentsService.deleteById(ps.getPaymentId());
				} else {
					ps.setPaymentAmount(amt.subtract(overpaid));
					overpaid = BigDecimal.ZERO;

					// inform payment service
					Payments payments = paymentsService.findById(ps.getPaymentId());
					payments.setPaymentAmount(ps.getPaymentAmount());
					payments.setPaymentMode(ps.getPaymentMode());
					paymentsService.save(payments);
				}
			}

			// Recalculate total paid after adjustment
			totalPaid = splits.stream().filter(p -> !"CREDIT".equalsIgnoreCase(p.getPaymentMode()))
					.map(p -> Optional.ofNullable(p.getPaymentAmount()).orElse(BigDecimal.ZERO))
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			totalCreditPaid = creditPayments.stream()
					.map(p -> Optional.ofNullable(p.getAmount()).orElse(BigDecimal.ZERO))
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			totalActualPaid = totalPaid.add(totalCreditPaid);

			// New pending
			BigDecimal newPending = newGrandTotal.subtract(totalActualPaid);
			invoice.setPendingAmount(newPending.max(BigDecimal.ZERO)); // no negative pending

			// Credit flags
			boolean hasCredit = splits.stream().anyMatch(p -> "CREDIT".equalsIgnoreCase(p.getPaymentMode()));

			invoice.setCreditFlag(hasCredit);
			invoice.setCreditSettledFlag(hasCredit && newPending.compareTo(BigDecimal.ZERO) == 0);
		}

		// Save back the updated invoice (pseudo code - use repo)
		invoiceRepository.save(invoice);
	}

	public JobCard createFastJobCard(FastJobCardRecord fastJobCard) throws Exception {
		JobCard jobCard = JobCard.builder()
				.ownerName(fastJobCard.ownerName())
				.ownerAddress(fastJobCard.ownerAddress())
				.ownerPhoneNumber(fastJobCard.ownerPhoneNumber())
				.vehicleName(fastJobCard.vehicleName())
				.vehicleRegNo(fastJobCard.vehicleRegNo())
				.kiloMeters(fastJobCard.kiloMeters())
				.jobStatus("OPEN")
				.build();
		jobCard = save(jobCard);

		JobSpares jobSpares = JobSpares.builder()
				.id(jobCard.getId())
				.jobId(jobCard.getJobId())
				.jobServiceInfo(fastJobCard.jobServiceInfo())
				.jobSparesInfo(fastJobCard.jobSparesInfo())
				.build();

		jobSpares = updateJobSpares(jobSpares);

		jobCard.setJobStatus("CLOSED");
		jobCard = updateJobStatus(jobCard);

		if (fastJobCard.billType().equals("ESTIMATE")) {
			estimateService.saveFastEstimate(jobCard, jobSpares, fastJobCard);
		} else if (fastJobCard.billType().equals("INVOICE")) {
			invoiceService.saveFastInvoice(jobCard, jobSpares, fastJobCard);
		}
		return jobCard;
	}

	public Map<String, String> getPhotoUrl(String id) {
		Map<String, String> result = new HashMap<>();
		// try {
		// Get the local host address
		// String ipAddress = InetAddress.getLocalHost().getHostAddress();
		// Construct the URL
		String url = "https://" + "rhineconstruction.in" + ":" + serverPort + "/jobCard/getPhotos/" + id;
		result.put("url", url);
		// } catch (UnknownHostException e) {
		// // Handle the exception if the IP address cannot be determined
		// throw new RuntimeException("Unable to determine the host IP address", e);
		// }
		return result;
	}

}

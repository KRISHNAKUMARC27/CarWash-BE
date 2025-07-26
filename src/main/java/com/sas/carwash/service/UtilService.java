package com.sas.carwash.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.sas.carwash.entity.Estimate;
import com.sas.carwash.entity.Invoice;
import com.sas.carwash.entity.JobCard;
import com.sas.carwash.entity.JobCardCounters;
import com.sas.carwash.entity.JobSpares;
import com.sas.carwash.model.PaymentSplit;
import com.sas.carwash.repository.JobCardRepository;
import com.sas.carwash.repository.JobSparesRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UtilService {

    private final MongoTemplate mongoTemplate;
    private final JobCardRepository jobCardRepository;
    private final JobSparesRepository jobSparesRepository;

    public int getNextSequenceForNewSequence(String sequenceName) {
        // Find the counter document and increment its sequence_value atomically
        Query query = new Query(Criteria.where("_id").is(sequenceName));
        JobCardCounters counter = mongoTemplate.findOne(query, JobCardCounters.class);

        if (counter == null) {
            counter = new JobCardCounters();
            counter.setId(sequenceName);
            counter.setSequenceValue(1);
            mongoTemplate.save(counter);
        } else {
            Query updateQuery = new Query(Criteria.where("_id").is(sequenceName));
            Update update = new Update().inc("sequenceValue", 1);
            FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
            counter = mongoTemplate.findAndModify(updateQuery, update, options, JobCardCounters.class);
            if (counter == null) {
                throw new RuntimeException("Error incrementing sequence for " + sequenceName);
            }
        }

        return counter.getSequenceValue();
    }

    public int getNextJobCardIdSequenceAsInteger(String sequenceName) {

        int currentYearMonth = Integer.parseInt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")));

        Query query = new Query(Criteria.where("_id").is(sequenceName));
        JobCardCounters counter = mongoTemplate.findOne(query, JobCardCounters.class);

        if (counter == null) {
            counter = new JobCardCounters();
            counter.setId(sequenceName);
            counter.setYearMonth(currentYearMonth);
            counter.setSequenceValue(1);
            mongoTemplate.save(counter);
        } else if (counter.getYearMonth() != currentYearMonth) {
            counter.setYearMonth(currentYearMonth);
            counter.setSequenceValue(1);
            mongoTemplate.save(counter);
        } else {
            Query updateQuery = new Query(Criteria.where("_id").is(sequenceName));
            Update update = new Update().inc("sequenceValue", 1);
            FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
            counter = mongoTemplate.findAndModify(updateQuery, update, options, JobCardCounters.class);
            if (counter == null) {
                throw new RuntimeException("Error incrementing sequence for " + sequenceName);
            }
        }

        // Concatenate yearMonth and sequenceValue
        int result = Integer.parseInt(currentYearMonth + String.format("%03d", counter.getSequenceValue()));

        // Return the final result as an integer
        return result;
    }

    public JobCard simpleSave(JobCard jobCard) {
        return jobCardRepository.save(jobCard);
    }

    public JobCard findById(String id) {
        return jobCardRepository.findById(id).orElse(null);
    }

    public JobSpares findByIdJobSpares(String id) {
        return jobSparesRepository.findById(id).orElse(null);
    }

    public JobSpares simpleSaveJobSpares(JobSpares jobSpares) {
        return jobSparesRepository.save(jobSpares);
    }

    public Invoice updatePaymentListForCreditInvoice(Invoice invoice, BigDecimal newPending) {
        if (BigDecimal.ZERO.compareTo(newPending) == 0) {
            return invoice; // No pending credit, skip modification
        }

        PaymentSplit split = PaymentSplit.builder().paymentAmount(newPending).paymentMode("CREDIT").flag("ADD")
                .paymentDate(LocalDateTime.now())
                .build();
        List<PaymentSplit> splitList = invoice.getPaymentSplitList();
        // What we are doing here is we are trying to maintain only 1 CREDIT mode item
        // in the payment list. Update with new Pending as CREDIT.
        if (splitList != null && !splitList.isEmpty()) {
            splitList = splitList.stream()
                    .filter(sp -> !"CREDIT".equals(sp.getPaymentMode()))
                    .collect(Collectors.toList());

            splitList.add(split);

        } else {
            splitList = new ArrayList<>();
            splitList.add(split);
        }
        invoice.setPaymentSplitList(splitList);
        return invoice;
    }

    public Estimate updatePaymentListForCreditEstimate(Estimate estimate, BigDecimal newPending) {
        if (BigDecimal.ZERO.compareTo(newPending) == 0) {
            return estimate; // No pending credit, skip modification
        }
        
        PaymentSplit split = PaymentSplit.builder().paymentAmount(newPending).paymentMode("CREDIT").flag("ADD")
                .paymentDate(LocalDateTime.now())
                .build();
        List<PaymentSplit> splitList = estimate.getPaymentSplitList();
        // What we are doing here is we are trying to maintain only 1 CREDIT mode item
        // in the payment list. Update with new Pending as CREDIT.
        if (splitList != null && !splitList.isEmpty()) {
            splitList = splitList.stream()
                    .filter(sp -> !"CREDIT".equals(sp.getPaymentMode()))
                    .collect(Collectors.toList());

            splitList.add(split);
        } else {
            splitList = new ArrayList<>();
            splitList.add(split);
        }

        estimate.setPaymentSplitList(splitList);
        return estimate;
    }

    public BigDecimal extractPackageDeductionAmount(List<PaymentSplit> paymentSplits) {
        if (paymentSplits == null)
            return BigDecimal.ZERO;

        return paymentSplits.stream()
                .filter(split -> "PACKAGE".equals(split.getPaymentMode()))
                .map(split -> split.getPaymentAmount() != null ? split.getPaymentAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String extractPackageDeductionPackageId(List<PaymentSplit> paymentSplits) {
        if (paymentSplits == null)
            return null;

        return paymentSplits.stream()
                .filter(split -> "PACKAGE".equalsIgnoreCase(split.getPaymentMode()))
                .map(PaymentSplit::getPaymentId)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

}

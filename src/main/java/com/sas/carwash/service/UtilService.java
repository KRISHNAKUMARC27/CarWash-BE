package com.sas.carwash.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.sas.carwash.entity.JobCard;
import com.sas.carwash.entity.JobCardCounters;
import com.sas.carwash.entity.JobSpares;
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
}

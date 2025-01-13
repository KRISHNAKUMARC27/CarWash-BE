package com.sas.carwash.repository;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.sas.carwash.entity.ServiceInventory;
import com.sas.carwash.entity.SparesInventory;

@Repository
public class ServiceInventoryFilterImpl implements ServiceInventoryFilter {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<ServiceInventory> findServiceInventoryWithFilter(List<String> categoryList, String desc) {

		Query query = new Query();

		if (categoryList != null && !categoryList.isEmpty()) {
			query.addCriteria(Criteria.where("category").in(categoryList));
		}
//		if (desc != null) {
//			query.addCriteria(Criteria.where("desc").in(desc));
//		}
		if (desc != null && !desc.isEmpty()) {
	        // Use a regular expression to allow partial matching
	        Pattern pattern = Pattern.compile(desc, Pattern.CASE_INSENSITIVE);
	        query.addCriteria(Criteria.where("desc").regex(pattern));
	    }

		return mongoTemplate.find(query, ServiceInventory.class);
	}
	
	public void updateCategory(String oldCategory, String newCategory) {
	    Query query = new Query(Criteria.where("category").is(oldCategory));
	    Update update = new Update().set("category", newCategory);
	    mongoTemplate.updateMulti(query, update, ServiceInventory.class);
	}

}

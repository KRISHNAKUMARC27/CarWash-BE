package com.sas.carwash.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.User;

public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);
}

package com.sas.carwash.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sas.carwash.entity.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
    Role findByName(String name);
}


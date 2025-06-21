package com.pcs.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pcs.model.Admin;

public interface AdminRepository extends MongoRepository<Admin, String> {
    Optional<Admin> findByUsername(String username);
}

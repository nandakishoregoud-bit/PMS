package com.pcs.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.pcs.model.Project;
import com.pcs.model.ProjectStatus;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {

    Page<Project> findByActiveTrue(Pageable pageable); // Get all active projects

    List<Project> findByStatus(String status); // Filter by project status

    List<Project> findByAssignedEmployeeIdsContaining(String employeeId); // Projects where employee is assigned
    
    long countByStatus(String status);
}

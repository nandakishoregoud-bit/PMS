package com.pcs.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.pcs.model.Task;


@Repository
public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByAssignedToEmployeeId(String employeeId);
    Page<Task> findByAssignedBy(String assignedBy, Pageable pageable);

    List<Task> findByAssignedBy(String employeeId);

    
}
package com.pcs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pcs.model.Employee;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
    List<Employee> findByActiveTrue();

	List<Employee> findByEmpIdContainingIgnoreCase(String empId);

	List<Employee> findByActiveFalse();

	Optional<Employee> findByEmail(String email);

	Optional<Employee> findByEmpId(String empId);

	Page<Employee> findByStatus(String status, Pageable pageable);


	long countByStatus(String status);

	Page<Employee> findByStatusAndActiveTrue(String status, Pageable pageable);


	long countByStatusAndActiveTrue(String status);

	long countByStatusAndActiveFalse(String status);

	List<Employee> findByEmpIdContainingIgnoreCaseAndActiveTrue(String empId);

	List<Employee> findByEmpIdContainingIgnoreCaseAndActiveTrueAndStatus(String empId, String string);
	
	Optional<Employee> findByEmpIdIgnoreCaseAndStatusAndActiveTrue(String empId, String status);


	Page<Employee> findByActiveFalse(Pageable pageable);

	List<Employee> searchByNameStartsWith(String search,String status);

	
	
}
package com.pcs.repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.pcs.model.LoginRecord;


@Repository
public interface LoginRecordRepository extends MongoRepository<LoginRecord, String> {

	Optional<LoginRecord> findTopByEmployeeIdAndLogoutTimeIsNullOrderByLoginTimeDesc(String employeeId);

	List<LoginRecord> findByEmployeeId(String employeeId);
	

	 Page<LoginRecord> findByEmployeeId(String employeeId, Pageable pageable);

	    Page<LoginRecord> findByLoginTimeBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

	    Page<LoginRecord> findByEmployeeIdAndLoginTimeBetween(String employeeId, LocalDateTime from, LocalDateTime to, Pageable pageable);
	
}
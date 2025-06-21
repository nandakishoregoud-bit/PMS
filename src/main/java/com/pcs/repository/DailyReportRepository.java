package com.pcs.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.pcs.model.DailyReport;


@Repository
public interface DailyReportRepository extends MongoRepository<DailyReport, String> {
    List<DailyReport> findByEmployeeId(String employeeId);
    List<DailyReport> findByEmployeeIdAndSubmittedDateBetween(String employeeId, LocalDateTime start, LocalDateTime end);

    List<DailyReport> findBySubmittedDateBetween(LocalDateTime from, LocalDateTime to);


}
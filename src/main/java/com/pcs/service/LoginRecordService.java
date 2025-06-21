package com.pcs.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;

import com.pcs.model.LoginRecord;


public interface LoginRecordService {
    LoginRecord recordLogin(String employeeId,String employeeName);
    List<LoginRecord> getLoginRecordsByEmployee(String employeeId);
    LoginRecord recordLogout(String employeeId);
    
    List<LoginRecord> getAllLoginRecords();
	Page<LoginRecord> searchLoginRecords(String employeeId, LocalDateTime from, LocalDateTime to, int page, int size);
	ByteArrayInputStream exportToPDF(List<LoginRecord> records) throws IOException;
	ByteArrayInputStream exportToExcel(List<LoginRecord> records) throws IOException;

    
}

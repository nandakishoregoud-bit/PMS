package com.pcs.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.pcs.model.DailyReport;


public interface DailyReportService {
    DailyReport submitReport(DailyReport report);
    List<DailyReport> getAllReports();
    List<DailyReport> getReportsByEmployee(String employeeId);
	List<DailyReport> getReportsByEmployeeAndMonthYear(String employeeId, int month, int year);
	List<DailyReport> getReportsByEmployeeAndYear(String employeeId, int year);
	
	void exportToExcel(List<DailyReport> reports, HttpServletResponse response, String fileName,String employeeId) throws IOException;
	void exportToPDF(List<DailyReport> reports, HttpServletResponse response, String fileName,String employeeId) throws IOException;

	
	List<DailyReport> getReportsBetweenDates(LocalDate from, LocalDate to);

}
package com.pcs.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pcs.model.DailyReport;
import com.pcs.service.DailyReportService;

@RestController
@RequestMapping("/api/admin/reports")
public class DailyReportController {

	@Autowired
	private DailyReportService dailyReportService;
	
	@PreAuthorize("hasRole('ADMIN')")
	// Get all daily reports
    @GetMapping("/dailyreports")
    public ResponseEntity<List<DailyReport>> getAllDailyReports() {
        return ResponseEntity.ok(dailyReportService.getAllReports());
    }

    // Get reports by employee ID
    @GetMapping("/dailyreport/{employeeId}")
    public ResponseEntity<List<DailyReport>> getReportsByEmployee(@PathVariable String employeeId) {
        return ResponseEntity.ok(dailyReportService.getReportsByEmployee(employeeId));
    }
    
 // Get reports by employee for a specific month & year
    @GetMapping("/dailyreport/{employeeId}/month/{month}/year/{year}")
    public ResponseEntity<List<DailyReport>> getMonthlyReports(
            @PathVariable String employeeId,
            @PathVariable int month,
            @PathVariable int year) {
        return ResponseEntity.ok(dailyReportService.getReportsByEmployeeAndMonthYear(employeeId, month, year));
    }

    // Get reports by employee for a specific year
    @GetMapping("/dailyreport/{employeeId}/year/{year}")
    public ResponseEntity<List<DailyReport>> getYearlyReports(
            @PathVariable String employeeId,
            @PathVariable int year) {
        return ResponseEntity.ok(dailyReportService.getReportsByEmployeeAndYear(employeeId, year));
    }
    
    @GetMapping("/dailyreport/{employeeId}/month/{month}/year/{year}/download")
    public void downloadMonthlyReport(
            @PathVariable String employeeId,
            @PathVariable int month,
            @PathVariable int year,
            @RequestParam(name = "format", defaultValue = "pdf") String format,
            HttpServletResponse response) throws IOException {

        List<DailyReport> reports = dailyReportService.getReportsByEmployeeAndMonthYear(employeeId, month, year);
        
        if (format.equalsIgnoreCase("excel")) {
            dailyReportService.exportToExcel(reports, response,employeeId, "Monthly_Report_" + month + "_" + year);
        } else {
            dailyReportService.exportToPDF(reports, response,employeeId, "Monthly_Report_" + month + "_" + year);
        }
    }

    @GetMapping("/dailyreport/{employeeId}/year/{year}/download")
    public void downloadYearlyReport(
            @PathVariable String employeeId,
            @PathVariable int year,
            @RequestParam(name = "format", defaultValue = "pdf") String format,
            HttpServletResponse response) throws IOException {

        List<DailyReport> reports = dailyReportService.getReportsByEmployeeAndYear(employeeId, year);

        if (format.equalsIgnoreCase("excel")) {
            dailyReportService.exportToExcel(reports, response,employeeId, "Yearly_Report_" + year);
        } else {
            dailyReportService.exportToPDF(reports, response,employeeId, "Yearly_Report_" + year);
        }
    }
    
    @GetMapping("/dailyreport/download")
    public void downloadReportsBetweenDates(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(name = "format", defaultValue = "pdf") String format,
            HttpServletResponse response) throws IOException {

        List<DailyReport> reports = dailyReportService.getReportsBetweenDates(from, to);

        String filename = "Reports_" + from.toString() + "_to_" + to.toString();

        if (format.equalsIgnoreCase("excel")) {
            dailyReportService.exportToExcel(reports, response, "ALL", filename);
        } else {
            dailyReportService.exportToPDF(reports, response, "ALL", filename);
        }
    }


    
}

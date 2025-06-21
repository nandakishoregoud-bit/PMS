package com.pcs.controller;

import java.time.LocalDateTime;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pcs.dto.EmployeeResponse;
import com.pcs.model.DailyReport;
import com.pcs.model.Employee;
import com.pcs.model.LoginRecord;
import com.pcs.model.Project;
import com.pcs.service.DailyReportService;
import com.pcs.service.EmployeeService;
import com.pcs.service.LoginRecordService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/admin/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private DailyReportService dailyReportService;
    
    @Autowired
    private LoginRecordService loginRecordService;
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Employee addEmployee(@RequestBody Employee employee) {
        return employeeService.addEmployee(employee);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Employee updateEmployee(@PathVariable String id, @RequestBody Employee employee) {
        return employeeService.updateEmployee(id, employee);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public Employee updateEmployeeStatus(@PathVariable String id) {
        return employeeService.updateStatus(id);
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<EmployeeResponse> getEmployee(@PathVariable String id){
    	return employeeService.getEmployee(id);
    }
    
    @PostMapping("/{id}/upload-profile-pic")
    public ResponseEntity<String> uploadProfilePic(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        try {
            String fileName = employeeService.saveProfilePicture(id, file);
            return ResponseEntity.ok("Profile picture uploaded successfully: " + fileName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deactivateEmployee(@PathVariable String id) {
        employeeService.deactivateEmployee(id);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/active")
    public String activateEmployee(@PathVariable String id) {
        return (employeeService.activateEmployee(id));
    }
    
    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeResponse>> getEmployees(@RequestParam(required = false) String search) {
        List<EmployeeResponse> result;
        
            result = employeeService.searchByEmpIdOrName(search);
        
        return ResponseEntity.ok(result);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Page<EmployeeResponse> getAllActiveEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return employeeService.getAllActiveEmployees(page, size);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/diactive")
    public Page<EmployeeResponse> getDiActiveEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return employeeService.getAllDeletedActiveEmployees(page, size);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/requests")
    public Page<EmployeeResponse> getAllDiActiveEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return employeeService.getAllDiActiveEmployees(page, size);
    }

    @PostMapping("/submitReport")
    public ResponseEntity<DailyReport> submitDailyReport(@RequestBody DailyReport report) {
        return ResponseEntity.ok(dailyReportService.submitReport(report));
    }
    
    @GetMapping("/loginreports")
    public ResponseEntity<Page<LoginRecord>> searchLoginReports(
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<LoginRecord> records = loginRecordService.searchLoginRecords(employeeId, from, to, page, size);
        return ResponseEntity.ok(records);
    }
    
    @GetMapping("/loginreports/export")
    public ResponseEntity<InputStreamResource> exportLoginReports(
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "pdf") String format
    ) throws IOException {
        List<LoginRecord> records = loginRecordService.searchLoginRecords(employeeId, from, to, 0, Integer.MAX_VALUE).getContent();

        String displayName = "";
        if (employeeId != null && !records.isEmpty()) {
            displayName = employeeId + "_" + records.get(0).getEmployeeName().replaceAll("\\s+", "_");
        }

        String filename = "login_record" + (displayName.isEmpty() ? "" : ("_" + displayName)) + "." + format;
        ByteArrayInputStream stream;

        if ("excel".equalsIgnoreCase(format)) {
            stream = loginRecordService.exportToExcel(records);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new InputStreamResource(stream));
        } else {
            stream = loginRecordService.exportToPDF(records);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(stream));
        }
    }


    
    
}
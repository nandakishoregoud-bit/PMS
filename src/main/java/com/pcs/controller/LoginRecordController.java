package com.pcs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pcs.model.LoginRecord;
import com.pcs.service.LoginRecordService;


@RestController
@RequestMapping("/api/login")
public class LoginRecordController {

    @Autowired
    private LoginRecordService loginRecordService;



    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LoginRecord>> getEmployeeLoginRecords(@PathVariable String employeeId) {
        return ResponseEntity.ok(loginRecordService.getLoginRecordsByEmployee(employeeId));
    }
    
    @PostMapping("/logout/{employeeId}")
    public ResponseEntity<LoginRecord> recordLogout(@PathVariable String employeeId) {
        return ResponseEntity.ok(loginRecordService.recordLogout(employeeId));
    }

    
}
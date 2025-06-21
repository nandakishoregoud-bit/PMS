package com.pcs.controller;

import java.util.Collections;
import java.util.Map;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pcs.dto.AdminRegisterRequest;
import com.pcs.dto.LoginRequest;
import com.pcs.dto.LoginResponse;
import com.pcs.dto.StatsResponse;
import com.pcs.model.Admin;
import com.pcs.repository.AdminRepository;
import com.pcs.repository.EmployeeRepository;
import com.pcs.repository.ProjectRepository;
import com.pcs.security.JwtUtil;
import com.pcs.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProjectRepository projectRepository;

    
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AdminRegisterRequest request) {
        try {
            Admin admin = adminService.register(request);
            return ResponseEntity.ok("Admin registered successfully with username: " + admin.getUsername());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStatistics() {
    	String status="Approved";
        long employeeCount = employeeRepository.countByStatusAndActiveTrue(status);
        long projectCount = projectRepository.count();
        long completedProjectCount = projectRepository.countByStatus("Completed"); // Adjust field name
        long exEmployeeCount = employeeRepository.countByStatusAndActiveFalse(status);

        StatsResponse stats = new StatsResponse(employeeCount, projectCount, completedProjectCount,exEmployeeCount);
        return ResponseEntity.ok(stats);
    }
    
    

}

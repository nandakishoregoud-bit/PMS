package com.pcs.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pcs.dto.LoginRequest;
import com.pcs.dto.LoginResponse;
import com.pcs.model.Employee;
import com.pcs.security.JwtUtil;
import com.pcs.service.EmployeeService;

@RestController
@RequestMapping("/api/auth/employees")
public class EmployeeAuthController {

	@Autowired
    private EmployeeService employeeService;
	
	@Autowired
    private JwtUtil jwtUtil;
	
	@PostMapping("/employee")
    public Employee registerEmployee(@RequestBody Employee employee) {
        return employeeService.registerEmployee(employee);
    }
	
	@PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = employeeService.login(request.getEmpId(), request.getPassword());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
    
	
	@PostMapping("/password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        return employeeService.forgotPassword(email);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(
        @RequestParam String email,
        @RequestParam String otp,
        @RequestParam String newPassword
    ) {
        return employeeService.verifyOtp(email, otp, newPassword);
    }
    
}

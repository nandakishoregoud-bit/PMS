package com.pcs.serviceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pcs.dto.EmployeeResponse;
import com.pcs.dto.LoginResponse;
import com.pcs.model.Admin;
import com.pcs.model.Employee;
import com.pcs.model.Project;
import com.pcs.repository.EmployeeRepository;
import com.pcs.security.JwtUtil;
import com.pcs.service.EmployeeService;
import com.pcs.service.LoginRecordService;

import io.jsonwebtoken.io.IOException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private LoginRecordService loginRecordService;

    
    @Override
    public Employee addEmployee(Employee employee) {
    	employee.setStatus("Approved");
        employee.setActive(true);
        employee.setRole("Employee");
        return employeeRepository.save(employee);
    }
    @Override
	public Employee registerEmployee(Employee employee) {
    	if (existsByEmail(employee.getEmail())) {
            throw new RuntimeException("Username already exists");
        }
    	if (existsByEmpId(employee.getEmpId())) {
    		throw new RuntimeException("Employee Id already exists");
    	}
    	employee.setPassword(passwordEncoder.encode(employee.getPassword()));
    	employee.setStatus("Pending");
    	employee.setActive(true);
    	employee.setRole("Employee");
        return employeeRepository.save(employee);
	}

    
	@Override
    public Employee updateEmployee(String id, Employee updatedEmployee) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
emp.setEmpId(updatedEmployee.getEmpId());
        emp.setName(updatedEmployee.getName());
        emp.setEmail(updatedEmployee.getEmail());
        emp.setDesignation(updatedEmployee.getDesignation());
        emp.setDepartment(updatedEmployee.getDepartment());
        emp.setSkills(updatedEmployee.getSkills());
        emp.setRole(updatedEmployee.getRole());

        return employeeRepository.save(emp);
    }
	@Override
	public Employee updateStatus(String id) {
		Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

		emp.setStatus("Approved");
		return employeeRepository.save(emp);
	}

    @Override
    public void deactivateEmployee(String id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        emp.setActive(false);
        employeeRepository.save(emp);
    }
    
    @Override
    public String activateEmployee(String id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        emp.setActive(true);
        employeeRepository.save(emp);
        String ret = "Employee Activeted Successfully";
		return ret;
    }
    
    @Override
    public ResponseEntity<EmployeeResponse> getEmployee(String id) {
        Employee emp = employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeResponse response = modelMapper.map(emp, EmployeeResponse.class);
        return ResponseEntity.ok(response);
    }
    


    @Override
    public Page<EmployeeResponse> getAllActiveEmployees(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employeePage = employeeRepository.findByStatusAndActiveTrue("Approved", pageable);

        return employeePage.map(emp -> modelMapper.map(emp, EmployeeResponse.class));
    }
    
    @Override
    public Page<EmployeeResponse> getAllDeletedActiveEmployees(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employeePage = employeeRepository.findByActiveFalse(pageable);

        return employeePage.map(emp -> modelMapper.map(emp, EmployeeResponse.class));
    }
    
    @Override
    public Page<EmployeeResponse> getAllDiActiveEmployees(int page, int size) {
        String status = "Pending";
        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employeePage = employeeRepository.findByStatusAndActiveTrue(status, pageable);

        List<EmployeeResponse> employeeResponses = employeePage.getContent().stream()
                .map(emp -> modelMapper.map(emp, EmployeeResponse.class))
                .collect(Collectors.toList());

        return new PageImpl<>(employeeResponses, pageable, employeePage.getTotalElements());
    }


    @Override
    public List<EmployeeResponse> searchByEmpIdOrName(String search) {
        if (search == null || search.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<Employee> employees;

        // First check if empId exactly matches (case-insensitive)
        Optional<Employee> byEmpId = employeeRepository.findByEmpIdIgnoreCaseAndStatusAndActiveTrue(search, "Approved");

        if (byEmpId.isPresent()) {
            employees = Collections.singletonList(byEmpId.get());
        } else {
            // Else try name starts with
            employees = employeeRepository.searchByNameStartsWith(search.trim().toLowerCase(), "Approved");
        }

        return employees.stream()
                .map(emp -> modelMapper.map(emp, EmployeeResponse.class))
                .collect(Collectors.toList());
    }


    
	

	@Override
    public boolean existsByEmail(String email) {
        return employeeRepository.findByEmail(email).isPresent();
    }
	private boolean existsByEmpId(String empId) {
		
		return employeeRepository.findByEmpId(empId).isPresent();
	}
	@Override
    public ResponseEntity<String> forgotPassword(String email) {
        Optional<Employee> optional = employeeRepository.findByEmail(email);
        if (!optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Employee employee = optional.get();
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit
        long expiry = System.currentTimeMillis() + 5 * 60 * 1000; // 5 mins

        employee.setOtp(otp);
        employee.setOtpExpiryTime(expiry);
        employeeRepository.save(employee);

        // Send email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP is: " + otp + ". It expires in 5 minutes.");
        mailSender.send(message);

        return ResponseEntity.ok("OTP sent to your email");
    }

    @Override
    public ResponseEntity<String> verifyOtp(String email, String otp, String newPassword) {
        Optional<Employee> optional = employeeRepository.findByEmail(email);
        if (!optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Employee employee = optional.get();

        if (!otp.equals(employee.getOtp())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP");
        }

        if (System.currentTimeMillis() > employee.getOtpExpiryTime()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP expired");
        }

        employee.setPassword(passwordEncoder.encode(newPassword));
        employee.setOtp(null);
        employee.setOtpExpiryTime(0);
        employeeRepository.save(employee);

        return ResponseEntity.ok("Password updated successfully");
    }
    @Override
    public LoginResponse login(String empId, String password) {
        Optional<Employee> employeeOpt = employeeRepository.findByEmpId(empId);

        if (employeeOpt.isPresent() && passwordEncoder.matches(password, employeeOpt.get().getPassword())) {
            Employee employee = employeeOpt.get();
            String token = jwtUtil.generateToken(employee.getEmpId(),employee.getRole());

            loginRecordService.recordLogin(employee.getEmpId(),employee.getName());
            
            return new LoginResponse(
                token,
                employee.getRole(),
                employee.getId(),
                employee.getEmpId()
            );
        } else {
            throw new IllegalArgumentException("Invalid credentials");
        }
    }
	@Override
	public boolean authenticate(String empId, String password) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public String saveProfilePicture(String employeeId, MultipartFile file) {
		try {
		Employee emp = employeeRepository.findByEmpId(employeeId)
	            .orElseThrow(() -> new RuntimeException("Employee not found"));

	    String uploadDir = "uploads/";
	    String originalFilename = file.getOriginalFilename();
	    String extension = "";
	    if (originalFilename != null && originalFilename.contains(".")) {
	        extension = originalFilename.substring(originalFilename.lastIndexOf("."));
	    }
	    String newFileName = UUID.randomUUID().toString() + extension;

	    // Delete old profile pic file if exists
	    if (emp.getProfilePic() != null) {
	        Path oldFilePath = Paths.get(uploadDir, emp.getProfilePic());
	        try {
				Files.deleteIfExists(oldFilePath);
			} catch (java.io.IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

	    // Save new file to disk
	    Path newFilePath = Paths.get(uploadDir, newFileName);
	    try {
			Files.copy(file.getInputStream(), newFilePath);
		} catch (java.io.IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    // Update employee profilePic field
	    emp.setProfilePic(newFileName);
	    employeeRepository.save(emp);

	    return newFileName;
		} catch (IOException e) {
	        throw new RuntimeException("Failed to save profile picture", e);
	    }
	}
	
	
} 	
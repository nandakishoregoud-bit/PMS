package com.pcs.service;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.pcs.dto.EmployeeResponse;
import com.pcs.dto.LoginResponse;
import com.pcs.model.Employee;
import com.pcs.model.Project;

public interface EmployeeService {
    Employee addEmployee(Employee employee);
    Employee updateEmployee(String id, Employee employee);
    void deactivateEmployee(String id);
    Page<EmployeeResponse> getAllActiveEmployees(int page, int size);
	List<EmployeeResponse> searchByEmpIdOrName(String search);
	Employee registerEmployee(Employee employee);
	Page<EmployeeResponse> getAllDiActiveEmployees(int page, int size);

	boolean existsByEmail(String email);
	Employee updateStatus(String id);
	ResponseEntity<String> forgotPassword(String email);
    ResponseEntity<String> verifyOtp(String email, String otp, String newPassword);
	boolean authenticate(String empId, String password);
	ResponseEntity<EmployeeResponse> getEmployee(String id);
	LoginResponse login(String empId, String password); 
	String saveProfilePicture(String id, MultipartFile file);
	String activateEmployee(String id);
	Page<EmployeeResponse> getAllDeletedActiveEmployees(int page, int size);
}

package com.pcs.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "employees")
public class Employee {

    @Id
    private String id;

    private String name;
    private String empId;
    private String email;
    private String designation;
    private String department;
    private String skills;
    private boolean active;
    private String status;
    private String role;
    private String Password;
    
    private String otp;
    private long otpExpiryTime;
    
    private String profilePic;
    
    private List<String> assignedTaskIds = new ArrayList<>();

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    
    public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public long getOtpExpiryTime() {
		return otpExpiryTime;
	}

	public void setOtpExpiryTime(long otpExpiryTime) {
		this.otpExpiryTime = otpExpiryTime;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	// Constructors
    public Employee() {}

    public Employee(String name, String email, String designation, String department,boolean active,String empId,String skills,String role) {
        this.name = name;
        this.email = email;
        this.designation = designation;
        this.department = department;
        this.active = active;
        this.empId = empId;
        this.skills=skills;
        this.role=role;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRole() {
		return role;
	}

	public List<String> getAssignedTaskIds() {
		return assignedTaskIds;
	}

	public void setAssignedTaskIds(List<String> assignedTaskIds) {
		this.assignedTaskIds = assignedTaskIds;
	}

	public void setRole(String role) {
		this.role = role;
	}


    
}
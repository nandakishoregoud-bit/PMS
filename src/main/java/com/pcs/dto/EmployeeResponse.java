package com.pcs.dto;

import org.springframework.data.annotation.Id;

public class EmployeeResponse {

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
    private String profilePic;

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    
   
	// Constructors
    public EmployeeResponse() {}

    public EmployeeResponse(String name, String email, String designation, String department,boolean active,String empId,String skills,String status,String role,String profilePic) {
        this.name = name;
        this.email = email;
        this.designation = designation;
        this.department = department;
        this.active = active;
        this.empId = empId;
        this.skills=skills;
        this.status=status;
        this.role=role;
        this.profilePic=profilePic;
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

	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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

	public void setRole(String role) {
		this.role = role;
	}
    
    

}

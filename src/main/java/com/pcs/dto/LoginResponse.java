package com.pcs.dto;

public class LoginResponse {
    private String token;
    private String role;
    private String id;
    private String empId;

    public LoginResponse(String token, String role, String id,String empId) {
        this.token = token;
        this.role = role;
        this.id = id;
        this.empId = empId;
    }
    
    public LoginResponse(String token) {
        this.token = token;
    }

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

    
}

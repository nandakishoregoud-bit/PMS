package com.pcs.model;


import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Document(collection = "login_records")
public class LoginRecord {

    @Id
    private String id;

    private String employeeId;
    private String employeeName;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime; // Optional: can track logout later
   // private boolean isWorkingHours; // true if login during work hours
    private String duration;

    public LoginRecord(String employeeId,String employeeName, LocalDateTime loginTime) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.loginTime = loginTime;
    }

	
	
	public String getDuration() {
		return duration;
	}



	public void setDuration(String duration) {
		this.duration = duration;
	}



	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	
	
	
	public LocalDateTime getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(LocalDateTime loginTime) {
		this.loginTime = loginTime;
	}



	public LocalDateTime getLogoutTime() {
		return logoutTime;
	}



	public void setLogoutTime(LocalDateTime logoutTime) {
		this.logoutTime = logoutTime;
	}



	public String getEmployeeName() {
		return employeeName;
	}



	public void setEmployeeName(String employeeName) {
		employeeName = employeeName;
	}

}
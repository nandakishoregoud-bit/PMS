package com.pcs.dto;


public class StatsResponse {
 private long employeeCount;
 private long projectCount;
 private long completedProjectCount;
 private long exEmployeeCount;

 public StatsResponse(long employeeCount, long projectCount, long completedProjectCount,long exEmployeeCount) {
     this.employeeCount = employeeCount;
     this.projectCount = projectCount;
     this.completedProjectCount = completedProjectCount;
     this.exEmployeeCount = exEmployeeCount;
 }

 // Getters and setters
 public long getEmployeeCount() {
     return employeeCount;
 }

 public void setEmployeeCount(long employeeCount) {
     this.employeeCount = employeeCount;
 }

 public long getProjectCount() {
     return projectCount;
 }

 public void setProjectCount(long projectCount) {
     this.projectCount = projectCount;
 }

 public long getCompletedProjectCount() {
     return completedProjectCount;
 }

 public void setCompletedProjectCount(long completedProjectCount) {
     this.completedProjectCount = completedProjectCount;
 }

public long getExEmployeeCount() {
	return exEmployeeCount;
}

public void setExEmployeeCount(long exEmployeeCount) {
	this.exEmployeeCount = exEmployeeCount;
}
}

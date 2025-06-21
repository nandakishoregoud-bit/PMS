package com.pcs.service;


import java.util.List;

import org.springframework.data.domain.Page;

import com.pcs.model.Project;

public interface ProjectService {

    Project createProject(Project project);

    Project updateProject(String projectId, Project updatedProject);

    void deleteProject(String projectId);

    Project assignEmployeeToProject(String projectId, String employeeId);

    Project removeEmployeeFromProject(String projectId, String employeeId);

    Project updateProjectStatus(String projectId, String status);

    Page<Project> getAllProjects(int page,int size);

    List<Project> getProjectsByStatus(String status);

    List<Project> getProjectsByEmployee(String empId);
}

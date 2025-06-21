package com.pcs.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pcs.model.Project;
import com.pcs.repository.ProjectRepository;
import com.pcs.service.ProjectService;

import java.util.*;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public Project createProject(Project project) {
        project.setId(null); // Ensure MongoDB auto-generates ID
        project.setActive(true);
        project.setCreatedAt(new Date());
        project.setUpdatedAt(new Date());
        return projectRepository.save(project);
    }

    @Override
    public Project updateProject(String projectId, Project updatedProject) {
        Project existing = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        existing.setName(updatedProject.getName());
        existing.setDescription(updatedProject.getDescription());
        existing.setStartDate(updatedProject.getStartDate());
        existing.setEndDate(updatedProject.getEndDate());
        existing.setStatus(updatedProject.getStatus());
        existing.setUpdatedAt(new Date());
        return projectRepository.save(existing);
    }

    @Override
    public void deleteProject(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        project.setActive(false); // Soft delete
        project.setUpdatedAt(new Date());
        projectRepository.save(project);
    }

    @Override
    public Project assignEmployeeToProject(String projectId, String employeeId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Safely initialize if null
        if (project.getAssignedEmployeeIds() == null) {
            project.setAssignedEmployeeIds(new ArrayList<>());
        }

        if (!project.getAssignedEmployeeIds().contains(employeeId)) {
            project.getAssignedEmployeeIds().add(employeeId);
        }

        return projectRepository.save(project);
    }


    @Override
    public Project removeEmployeeFromProject(String projectId, String employeeId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        project.getAssignedEmployeeIds().remove(employeeId);
        project.setUpdatedAt(new Date());
        return projectRepository.save(project);
    }

    @Override
    public Project updateProjectStatus(String projectId, String status) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        project.setStatus(status);
        project.setUpdatedAt(new Date());
        return projectRepository.save(project);
    }

    @Override
    public Page<Project> getAllProjects(int page, int size) {
    	Pageable pageable = PageRequest.of(page, size);
        return projectRepository.findByActiveTrue(pageable);
    }

    @Override
    public List<Project> getProjectsByStatus(String status) {
        return projectRepository.findByStatus(status);
    }

    @Override
    public List<Project> getProjectsByEmployee(String empId) {
        return projectRepository.findByAssignedEmployeeIdsContaining(empId);
    }
}

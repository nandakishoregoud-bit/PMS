package com.pcs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.pcs.model.Employee;
import com.pcs.model.Project;
import com.pcs.repository.ProjectRepository;
import com.pcs.service.ProjectService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/admin/projects")
@CrossOrigin(origins = "*") // Adjust as needed
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;
    
    // Create a new project
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public Project createProject(@RequestBody Project project) {
        return projectService.createProject(project);
    }

    // Update project information
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{projectId}")
    public Project updateProject(@PathVariable String projectId, @RequestBody Project updatedProject) {
        return projectService.updateProject(projectId, updatedProject);
    }

    // Delete (soft-delete) a project
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{projectId}")
    public String deleteProject(@PathVariable String projectId) {
        projectService.deleteProject(projectId);
        return "Project marked as inactive.";
    }

    // Assign employee to project
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{projectId}/assign/{employeeId}")
    public Project assignEmployee(@PathVariable String projectId, @PathVariable String employeeId) {
        System.out.println(employeeId);
    	return projectService.assignEmployeeToProject(projectId, employeeId);
    }

    // Remove employee from project
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{projectId}/remove/{employeeId}")
    public Project removeEmployee(@PathVariable String projectId, @PathVariable String employeeId) {
        return projectService.removeEmployeeFromProject(projectId, employeeId);
    }

    // Update project status
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{projectId}/status")
    public Project updateStatus(@PathVariable String projectId, @RequestParam String status) {
        return projectService.updateProjectStatus(projectId, status);
    }

    // Get all active projects
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public Page<Project> getAllProjects(
    		@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return projectService.getAllProjects(page,size);
    }
    
    

    // Get projects by status
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/status/{status}")
    public List<Project> getByStatus(@PathVariable String status) {
        return projectService.getProjectsByStatus(status);
    }
    


    // Get projects by employee ID
    @GetMapping("/employee/{empId}")
    public List<Project> getByEmployee(@PathVariable String empId) {
        return projectService.getProjectsByEmployee(empId);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/projects/{id}/upload")
    public ResponseEntity<?> uploadFiles(
            @PathVariable String id,
            @RequestParam("files") List<MultipartFile> files) {
        Optional<Project> projectOpt = projectRepository.findById(id);
        if (!projectOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Project project = projectOpt.get();
        List<String> uploadedPaths = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
            	String sanitizedFileName = file.getOriginalFilename().replaceAll("\\s+", "_");
            	String fileName = UUID.randomUUID() + "_" + sanitizedFileName;

            	Path filePath = Paths.get("uploads", fileName);
            	Files.createDirectories(filePath.getParent());
            	Files.write(filePath, file.getBytes());

            	// Save using forward slashes for easier URL decoding later
            	uploadedPaths.add(filePath.toString().replace("\\", "/"));

            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to upload file: " + file.getOriginalFilename());
            }
        }

        if (project.getFilePaths() == null) project.setFilePaths(new ArrayList<>());
        project.getFilePaths().addAll(uploadedPaths);
        projectRepository.save(project);

        return ResponseEntity.ok("Files uploaded successfully");
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/projects/{projectId}/files/{fileName:.+}")
    public ResponseEntity<UrlResource> getFileFromProject(
            @PathVariable String projectId,
            @PathVariable String fileName) {

        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (!projectOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Project project = projectOpt.get();

        String matchedPath = project.getFilePaths().stream()
                .filter(path -> Paths.get(path).getFileName().toString().equals(fileName))
                .findFirst()
                .orElse(null);

        if (matchedPath == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try {
            Path filePath = Paths.get(System.getProperty("user.dir")).resolve(matchedPath).normalize();
            UrlResource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/projects/{id}/file")
    public ResponseEntity<?> deleteFile(
            @PathVariable String id,
            @RequestParam String filePath) {
        Optional<Project> projectOpt = projectRepository.findById(id);
        if (!projectOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Project project = projectOpt.get();
        if (project.getFilePaths() != null && project.getFilePaths().remove(filePath)) {
            projectRepository.save(project);
            try {
                Files.deleteIfExists(Paths.get(filePath));
            } catch (IOException e) {
                // Ignore file deletion error
            }
            return ResponseEntity.ok("File deleted");
        }

        return ResponseEntity.badRequest().body("File not found in project");
    }

}

package com.pcs.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pcs.model.Comment;
import com.pcs.model.Employee;
import com.pcs.model.Task;
import com.pcs.repository.EmployeeRepository;
import com.pcs.repository.TaskRepository;
import com.pcs.service.TaskService;


@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TaskRepository taskRepository;

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping("/createtask")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        return ResponseEntity.ok(taskService.assignTask(task));
    }
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PutMapping("/{empId}/update/{taskId}")
    public ResponseEntity<Task> updateTask(@PathVariable String empId,@PathVariable String taskId, @RequestBody Task updatedTask) {
        return ResponseEntity.ok(taskService.updateTask(empId,taskId, updatedTask));
    }
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @DeleteMapping("/{empId}/delete/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable String empId,@PathVariable String taskId) {
    	String result=taskService.deleteTask(empId,taskId);
    	if (result.equals("Deleted")) {
	        return ResponseEntity.ok(result);
	    } else {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
	    }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Task>> getTasksByEmployee(@PathVariable String employeeId) {
        return ResponseEntity.ok(taskService.getTasksByEmployeeId(employeeId));
    }
    
    @GetMapping("/{employeeId}/{taskId}")
    public ResponseEntity<?> getTaskByEmployeeAndTaskId(@PathVariable String employeeId, @PathVariable String taskId) {
        Optional<Employee> employeeOpt = employeeRepository.findByEmpId(employeeId);
        
        if (employeeOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Employee not found");
        }

        Employee emp = employeeOpt.get();
        if (!emp.getAssignedTaskIds().contains(taskId)) {
            return ResponseEntity.status(403).body("Task not assigned to this employee");
        }

        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            return ResponseEntity.ok(taskOpt.get());
        } else {
            return ResponseEntity.status(404).body("Task not found");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Page<Task>> getAllTasks(
    		@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    		) {
        return ResponseEntity.ok(taskService.getAllTasks(page ,size));
    }
    
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("{employeeId}/all")
    public ResponseEntity<Page<Task>> getAllTasksById(
            @PathVariable String employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Optional<Employee> employeeOpt = employeeRepository.findByEmpId(employeeId);
        if (employeeOpt.isEmpty()) {
            throw new IllegalStateException("Employee is not approved");
        }

        return ResponseEntity.ok(taskService.getAllTasksById(employeeId, page, size));
    }

    
    @GetMapping("{taskId}/all/comments")
    public ResponseEntity<List<Comment>> getAllComments(@PathVariable String taskId){
    	Optional<Task> task = taskRepository.findById(taskId);
    	if (task.isEmpty()) {
        	throw new IllegalStateException("Employee is not approved");
        }else {
        	return ResponseEntity.ok(taskService.getCommentsByTaskId(taskId));
        }
    }
    
    @PutMapping("{taskId}/updateStatus")
    public ResponseEntity<String> updateStatus(@PathVariable String taskId, @RequestParam String status) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        } else {
            taskService.updateTaskStatus(taskId, status);
            return ResponseEntity.ok("Task status updated to " + status);
        }
    }

}

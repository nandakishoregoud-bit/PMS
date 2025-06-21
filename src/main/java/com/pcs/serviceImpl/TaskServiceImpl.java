package com.pcs.serviceImpl;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.pcs.model.Comment;
import com.pcs.model.Employee;
import com.pcs.model.Task;
import com.pcs.repository.CommentRepository;
import com.pcs.repository.EmployeeRepository;
import com.pcs.repository.TaskRepository;
import com.pcs.service.TaskService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Task assignTask(Task task) {
        List<String> employeeIds = task.getAssignedToEmployeeId();

        // Validate all employee IDs exist
        for (String empId : employeeIds) {
            Optional<Employee> employeeOpt = employeeRepository.findByEmpId(empId);
            if (employeeOpt.isEmpty()) {
                throw new RuntimeException("Employee not found with ID: " + empId);
            }
        }

        // Set current date/time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentDate = LocalDateTime.now().format(formatter);
        task.setAssignedDate(currentDate);

        // Save the task first to get its ID
        Task savedTask = taskRepository.save(task);

        // Update each assigned employee's task list
        for (String empId : employeeIds) {
            Employee emp = employeeRepository.findByEmpId(empId).get();
            List<String> taskIds = emp.getAssignedTaskIds();
            taskIds.add(savedTask.getId());
            emp.setAssignedTaskIds(taskIds);
            employeeRepository.save(emp);
        }

        return savedTask;
    }


    @Override
    public List<Task> getTasksByEmployeeId(String employeeId) {
        return taskRepository.findByAssignedToEmployeeId(employeeId);
    }

    @Override
    public Page<Task> getAllTasks(int page, int size) {
    	Pageable pageable = PageRequest.of(page, size, Sort.by("assignedDate").descending());
        return taskRepository.findAll(pageable);
    }

    @Override
    public Page<Task> getAllTasksById(String employeeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("assignedDate").descending());
        return taskRepository.findByAssignedBy(employeeId, pageable);
    }


	@Override
	public List<Comment> getCommentsByTaskId(String taskId) {
		
		return commentRepository.findByTaskId(taskId);
	}

	@Override
	public String updateTaskStatus(String taskId, String status) {
	    Optional<Task> taskOpt = taskRepository.findById(taskId);
	    if (taskOpt.isPresent()) {
	        Task task = taskOpt.get();
	        task.setStatus(status);
	        taskRepository.save(task); 
	        return "Status updated successfully";
	    } else {
	        throw new NoSuchElementException("Task with ID " + taskId + " not found.");
	    }
	}
	
	@Override
	public Task updateTask(String empId, String taskId, Task updatedTask) {
	    Task existingTask = taskRepository.findById(taskId)
	        .orElseThrow(() -> new NoSuchElementException("Task not found with id: " + taskId));

	    if (existingTask.getAssignedBy().equals(empId)) {
	        existingTask.setTask(updatedTask.getTask());
	        existingTask.setDescription(updatedTask.getDescription());
	        existingTask.setAssignedToEmployeeId(updatedTask.getAssignedToEmployeeId());
	        existingTask.setAssignedDate(updatedTask.getAssignedDate());
	        existingTask.setDueDate(updatedTask.getDueDate());
	        

	        return taskRepository.save(existingTask);
	    } else {
	        throw new SecurityException("You are not the owner who assigned this task." +empId);
	    }
	}


	@Override
	public String deleteTask(String empId,String taskId) {
	    Task task = taskRepository.findById(taskId)
	        .orElseThrow(() -> new NoSuchElementException("Task not found with id: " + taskId));
	    if(task.getAssignedBy().equals(empId)) {
	    taskRepository.delete(task);
	    return ("Deleted");
		}else {
			return ("Your not the owner of the Task");
		}
	}


}

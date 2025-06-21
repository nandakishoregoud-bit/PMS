package com.pcs.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.pcs.model.Comment;
import com.pcs.model.Task;


public interface TaskService {
	Task assignTask(Task task);
    List<Task> getTasksByEmployeeId(String employeeId);
    Page<Task> getAllTasks(int page, int size);
	List<Comment> getCommentsByTaskId(String taskId);
	String updateTaskStatus(String taskId,String status);
	
	Task updateTask(String empId,String taskId, Task updatedTask);

	String deleteTask(String empId,String taskId);
	Page<Task> getAllTasksById(String employeeId, int page, int size);

}

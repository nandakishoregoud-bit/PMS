package com.pcs.serviceImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pcs.model.Comment;
import com.pcs.model.Employee;
import com.pcs.repository.CommentRepository;
import com.pcs.repository.EmployeeRepository;
import com.pcs.service.CommentService;

@Service
public class CommentServiceImpl implements CommentService {
	
	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private EmployeeRepository employeeRepository;

	@Override
	public Comment submitComment(Comment comment) {
		String empId = comment.getSubmittedBy();
		Optional<Employee> employeeOpt = employeeRepository.findByEmpId(empId);

        if (employeeOpt.isPresent()) {
		LocalDateTime now = LocalDateTime.now();
		comment.setSubmittedDate(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		return commentRepository.save(comment);
        }else {
            throw new IllegalArgumentException("Employee not found with ID: " + empId);
        }
        
	}

	@Override
	public String deleteComment(String empId,String commentId) {
		
		Comment comment = commentRepository.findById(commentId)
			    .orElseThrow(() -> new NoSuchElementException("Comment not found with id: " + commentId));

		if(comment.getSubmittedBy().equals(empId)) {
		commentRepository.delete(comment);
		return ("Deleted");
		}else {
			return ("Your not the owner of the Comment");
		}
	}
	
	

}

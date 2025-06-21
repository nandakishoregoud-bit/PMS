package com.pcs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pcs.model.Comment;
import com.pcs.service.CommentService;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
	
	@Autowired
	private CommentService commentService;
	
	@PostMapping("/post")
	public ResponseEntity<Comment> submitComment(@RequestBody Comment comment){
		
		return ResponseEntity.ok(commentService.submitComment(comment));
	}
	
	@DeleteMapping("/{empId}/delete/{commentId}")
	public ResponseEntity<String> deleteComment(@PathVariable String empId, @PathVariable String commentId) {
	    String result = commentService.deleteComment(empId, commentId);
	    if (result.equals("Deleted")) {
	        return ResponseEntity.ok(result);
	    } else {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
	    }
	}

 
}

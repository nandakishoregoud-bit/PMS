package com.pcs.service;

import com.pcs.model.Comment;

public interface CommentService {

	Comment submitComment(Comment comment);

	String deleteComment(String empId,String commentId);
	

}

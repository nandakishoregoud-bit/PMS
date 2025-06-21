package com.pcs.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.pcs.model.Comment;


@Repository
public interface CommentRepository extends MongoRepository<Comment, String>{
	
	List<Comment> findByTaskId(String taskId);

}

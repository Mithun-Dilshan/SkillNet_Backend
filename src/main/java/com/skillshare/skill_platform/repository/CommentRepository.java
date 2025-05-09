package com.skillshare.skill_platform.repository;

import com.skillshare.skill_platform.entity.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment,String> {
    List<Comment> findByUserId(String userId);
    long countByUserId(String userId);
} 
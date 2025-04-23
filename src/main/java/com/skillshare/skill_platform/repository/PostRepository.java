package com.skillshare.skill_platform.repository;

import com.skillshare.skill_platform.entity.Post;
import org.apache.catalina.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRepository extends MongoRepository<Post,String> {
} 
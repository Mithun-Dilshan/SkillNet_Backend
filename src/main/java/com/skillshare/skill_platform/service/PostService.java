package com.skillshare.skill_platform.service;

import com.skillshare.skill_platform.dto.PostRequest;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface PostService {
    public ResponseEntity<Map> createPost(PostRequest postRequest);
    public ResponseEntity<Map> getPost(String postId);
    ResponseEntity<Map> getAllPosts();
    public ResponseEntity<Map> updatePost(String postId, PostRequest postRequest);
    public ResponseEntity<Map> deletePost(String postId);
    ResponseEntity<Map> likePost(String postId, String userId);
    ResponseEntity<Map> unlikePost(String postId, String userId);
} 
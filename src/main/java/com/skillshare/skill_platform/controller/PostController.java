package com.skillshare.skill_platform.controller;

import com.skillshare.skill_platform.dto.PostRequest;
import com.skillshare.skill_platform.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @PostMapping("/upload")
    public ResponseEntity<Map> upload(PostRequest postRequest) {
        try {
            return postService.createPost(postRequest);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Map> getPost(@PathVariable String postId) {
        return postService.getPost(postId);
    }

    @GetMapping
    public ResponseEntity<Map> getAllPosts() {
        System.out.println("ok");
        return postService.getAllPosts();
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Map> updatePost(@PathVariable String postId, @RequestBody PostRequest postRequest) {
        return postService.updatePost(postId, postRequest);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Map> deletePost(@PathVariable String postId) {
        return postService.deletePost(postId);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Map> likePost(@PathVariable String postId, @RequestParam String userId) {
        return postService.likePost(postId, userId);
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Map> unlikePost(@PathVariable String postId, @RequestParam String userId) {
        return postService.unlikePost(postId, userId);
    }
} 
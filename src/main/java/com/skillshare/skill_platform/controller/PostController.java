package com.skillshare.skill_platform.controller;

import com.skillshare.skill_platform.dto.PostRequest;
import com.skillshare.skill_platform.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<Map> upload(
            @RequestParam("userId") String userId,
            @RequestParam("description") String description,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            PostRequest postRequest = new PostRequest();
            postRequest.setUserId(userId);
            postRequest.setDescription(description);
            postRequest.setFile(file);
            
            System.out.println("Received post upload request: userId=" + userId + 
                               ", description=" + description + 
                               ", file=" + (file != null ? file.getOriginalFilename() : "none"));
            
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
        System.out.println("Fetching all posts");
        return postService.getAllPosts();
    }

    @PutMapping(value = "/{postId}", consumes = {"multipart/form-data"})
    public ResponseEntity<Map> updatePost(
            @PathVariable String postId,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        
        PostRequest postRequest = new PostRequest();
        postRequest.setUserId(userId);
        postRequest.setDescription(description);
        postRequest.setFile(file);
        
        System.out.println("Updating post: " + postId);
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
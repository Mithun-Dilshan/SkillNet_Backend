package com.skillshare.skill_platform.service.Impl;

import com.skillshare.skill_platform.dto.PostRequest;
import com.skillshare.skill_platform.entity.Post;
import com.skillshare.skill_platform.entity.Like;
import com.skillshare.skill_platform.repository.PostRepository;
import com.skillshare.skill_platform.service.CloudinaryService;
import com.skillshare.skill_platform.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private PostRepository postRepository;

    @Override
    public ResponseEntity<Map> createPost(PostRequest postRequest) {
        try {
            System.out.println("Creating new post: " + postRequest);
            
            if (postRequest.getDescription() == null || postRequest.getDescription().isEmpty()) {
                System.err.println("Post creation failed: Description is empty");
                return ResponseEntity.badRequest().body(Map.of("error", "Description cannot be empty"));
            }
            
            Post post = new Post();
            post.setUserId(postRequest.getUserId());
            post.setDescription(postRequest.getDescription());
            
            // Only upload file if it exists
            MultipartFile file = postRequest.getFile();
            if (file != null && !file.isEmpty()) {
                System.out.println("Uploading file: " + file.getOriginalFilename() + " (" + file.getSize() + " bytes)");
                String uploadedUrl = cloudinaryService.uploadFile(file, "posts");
                
                if (uploadedUrl == null) {
                    System.err.println("Post creation failed: File upload to Cloudinary failed");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", "Failed to upload image"));
                }
                
                System.out.println("File upload successful, URL: " + uploadedUrl);
                post.setUrl(uploadedUrl);
            } else {
                System.out.println("No file provided for upload");
            }
            
            post.setDate(new Date());
            Post savedPost = postRepository.save(post);
            
            System.out.println("Post created successfully with ID: " + savedPost.getId());
            return ResponseEntity.ok().body(Map.of(
                "message", "Post created successfully",
                "post", savedPost
            ));
        } catch (Exception e) {
            System.err.println("Post creation failed with exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create post: " + e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<Map> getPost(String postId) {
        try {
            Optional<Post> optionalPost = postRepository.findById(postId);
            if (optionalPost.isPresent()) {
                Post post = optionalPost.get();
                return ResponseEntity.ok().body(Map.of("post", post));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<Map> getAllPosts() {
        try {
            List<Post> posts = postRepository.findAll();
            // Sort posts by date in descending order (newest first)
            posts.sort((a, b) -> b.getDate().compareTo(a.getDate()));
            return ResponseEntity.ok().body(Map.of("posts", posts));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<Map> updatePost(String postId, PostRequest postRequest) {
        try {
            System.out.println("Updating post with ID: " + postId);
            
            Optional<Post> optionalPost = postRepository.findById(postId);
            if (!optionalPost.isPresent()) {
                System.err.println("Post update failed: Post not found with ID: " + postId);
                return ResponseEntity.notFound().build();
            }

            Post post = optionalPost.get();

            if (postRequest.getDescription() != null && !postRequest.getDescription().isEmpty()) {
                post.setDescription(postRequest.getDescription());
            }

            MultipartFile file = postRequest.getFile();
            if (file != null && !file.isEmpty()) {
                System.out.println("Uploading new file for post update: " + file.getOriginalFilename());
                String uploadedUrl = cloudinaryService.uploadFile(file, "posts");
                
                if (uploadedUrl == null) {
                    System.err.println("Post update failed: File upload to Cloudinary failed");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", "Failed to upload image"));
                }
                
                System.out.println("File upload successful for post update, URL: " + uploadedUrl);
                post.setUrl(uploadedUrl);
            }

            Post updatedPost = postRepository.save(post);
            System.out.println("Post updated successfully: " + updatedPost.getId());
            
            return ResponseEntity.ok().body(Map.of(
                "message", "Post updated successfully",
                "post", updatedPost
            ));
        } catch (Exception e) {
            System.err.println("Post update failed with exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update post: " + e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<Map> deletePost(String postId) {
        try {
            Optional<Post> optionalPost = postRepository.findById(postId);
            if (optionalPost.isPresent()) {
                postRepository.deleteById(postId);
                return ResponseEntity.ok().body(Map.of("message", "Post deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<Map> likePost(String postId, String userId) {
        try {
            Optional<Post> optionalPost = postRepository.findById(postId);
            if (!optionalPost.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Post post = optionalPost.get();
            
            // Check if user has already liked the post
            boolean alreadyLiked = post.getLikes().stream()
                .anyMatch(like -> like.getUserId().equals(userId));
            
            if (alreadyLiked) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "User has already liked this post"));
            }

            // Create new like
            Like like = new Like();
            like.setPostId(postId);
            like.setUserId(userId);
            like.setCreatedAt(new Date());

            // Add like to post
            post.getLikes().add(like);
            postRepository.save(post);

            return ResponseEntity.ok()
                .body(Map.of(
                    "message", "Post liked successfully",
                    "post", post
                ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<Map> unlikePost(String postId, String userId) {
        try {
            Optional<Post> optionalPost = postRepository.findById(postId);
            if (!optionalPost.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Post post = optionalPost.get();
            
            // Remove like from post
            boolean removed = post.getLikes().removeIf(like -> like.getUserId().equals(userId));
            
            if (!removed) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "User has not liked this post"));
            }

            postRepository.save(post);

            return ResponseEntity.ok()
                .body(Map.of(
                    "message", "Post unliked successfully",
                    "post", post
                ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 
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
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;

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
            if (optionalPost.isPresent()) {
                Post post = optionalPost.get();
                
<<<<<<< HEAD
                // Check if user already liked this post
=======
>>>>>>> main
                boolean alreadyLiked = post.getLikes().stream()
                    .anyMatch(like -> like.getUserId().equals(userId));
                
                if (!alreadyLiked) {
                    Like like = new Like();
                    like.setId(UUID.randomUUID().toString());
                    like.setPostId(postId);
                    like.setUserId(userId);
                    like.setCreatedAt(new Date());
                    
                    post.getLikes().add(like);
                    Post updatedPost = postRepository.save(post);
                    
                    return ResponseEntity.ok().body(Map.of(
                        "message", "Post liked successfully",
                        "post", updatedPost
                    ));
                } else {
                    return ResponseEntity.ok().body(Map.of(
                        "message", "Post already liked by this user",
                        "post", post
                    ));
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<Map> unlikePost(String postId, String userId) {
        try {
            Optional<Post> optionalPost = postRepository.findById(postId);
            if (optionalPost.isPresent()) {
                Post post = optionalPost.get();
                
                List<Like> updatedLikes = post.getLikes().stream()
                    .filter(like -> !like.getUserId().equals(userId))
                    .collect(Collectors.toList());
                
                post.setLikes(updatedLikes);
                Post updatedPost = postRepository.save(post);
                
                return ResponseEntity.ok().body(Map.of(
                    "message", "Post unliked successfully",
                    "post", updatedPost
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Override
    public ResponseEntity<Map> savePost(String postId, String userId) {
        try {
            System.out.println("Saving post with ID: " + postId + " for user: " + userId);
            
            Optional<Post> optionalPost = postRepository.findById(postId);
            if (!optionalPost.isPresent()) {
                System.err.println("Post save failed: Post not found with ID: " + postId);
                return ResponseEntity.notFound().build();
            }

            Post post = optionalPost.get();
            
<<<<<<< HEAD
            // Initialize savedByUsers if null
=======
>>>>>>> main
            if (post.getSavedByUsers() == null) {
                post.setSavedByUsers(new ArrayList<>());
            }
            
<<<<<<< HEAD
            // Check if already saved by this user
=======
>>>>>>> main
            if (!post.getSavedByUsers().contains(userId)) {
                post.getSavedByUsers().add(userId);
                System.out.println("Added user " + userId + " to saved list for post " + postId);
            } else {
                System.out.println("Post " + postId + " already saved by user " + userId);
                return ResponseEntity.ok().body(Map.of(
                    "message", "Post already saved by this user",
                    "post", post
                ));
            }
            
            Post updatedPost = postRepository.save(post);
            System.out.println("Post saved successfully by user: " + userId);
            
            return ResponseEntity.ok().body(Map.of(
                "message", "Post saved successfully",
                "post", updatedPost
            ));
        } catch (Exception e) {
            System.err.println("Post save failed with exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to save post: " + e.getMessage()));
        }
    }
    
    @Override
    public ResponseEntity<Map> unsavePost(String postId, String userId) {
        try {
            System.out.println("Unsaving post with ID: " + postId + " for user: " + userId);
            
            Optional<Post> optionalPost = postRepository.findById(postId);
            if (!optionalPost.isPresent()) {
                System.err.println("Post unsave failed: Post not found with ID: " + postId);
                return ResponseEntity.notFound().build();
            }

            Post post = optionalPost.get();
            
<<<<<<< HEAD
            // Initialize savedByUsers if null
=======
>>>>>>> main
            if (post.getSavedByUsers() == null) {
                post.setSavedByUsers(new ArrayList<>());
                System.out.println("Post " + postId + " not saved by any user");
                return ResponseEntity.ok().body(Map.of(
                    "message", "Post was not saved by this user",
                    "post", post
                ));
            }
            
            // Remove the user from saved list
            if (post.getSavedByUsers().contains(userId)) {
                post.getSavedByUsers().remove(userId);
                System.out.println("Removed user " + userId + " from saved list for post " + postId);
            } else {
                System.out.println("Post " + postId + " was not saved by user " + userId);
                return ResponseEntity.ok().body(Map.of(
                    "message", "Post was not saved by this user",
                    "post", post
                ));
            }
            
            Post updatedPost = postRepository.save(post);
            System.out.println("Post unsaved successfully by user: " + userId);
            
            return ResponseEntity.ok().body(Map.of(
                "message", "Post unsaved successfully",
                "post", updatedPost
            ));
        } catch (Exception e) {
            System.err.println("Post unsave failed with exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to unsave post: " + e.getMessage()));
        }
    }
<<<<<<< HEAD
=======
    
    @Override
    public ResponseEntity<Map> getPostsByUserId(String userId) {
        try {
            System.out.println("Fetching posts for user: " + userId);
            
            if (userId == null || userId.isEmpty()) {
                System.err.println("Failed to fetch posts: userId is empty");
                return ResponseEntity.badRequest().body(Map.of("error", "User ID cannot be empty"));
            }
            
            List<Post> posts = postRepository.findByUserId(userId);
            
            posts.sort((a, b) -> b.getDate().compareTo(a.getDate()));
            
            System.out.println("Found " + posts.size() + " posts for user: " + userId);
            
            return ResponseEntity.ok().body(Map.of("posts", posts));
        } catch (Exception e) {
            System.err.println("Failed to fetch posts for user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch posts: " + e.getMessage()));
        }
    }
>>>>>>> main
} 
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
            if (postRequest.getDescription() == null || postRequest.getDescription().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            Post post = new Post();
            post.setUserId(postRequest.getUserId());
            post.setDescription(postRequest.getDescription());
            
            // Only upload file if it exists
            if (postRequest.getFile() != null && !postRequest.getFile().isEmpty()) {
                String uploadedUrl = cloudinaryService.uploadFile(postRequest.getFile(), "folder_1");
                if (uploadedUrl == null) {
                    return ResponseEntity.badRequest().build();
                }
                post.setUrl(uploadedUrl);
            }
            
            post.setDate(new Date());
            Post savedPost = postRepository.save(post);
            
            return ResponseEntity.ok().body(Map.of("post", savedPost));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
            Optional<Post> optionalPost = postRepository.findById(postId);
            if (!optionalPost.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Post post = optionalPost.get();

           
            if (postRequest.getDescription() != null && !postRequest.getDescription().isEmpty()) {
                post.setDescription(postRequest.getDescription());
            }

           
            if (postRequest.getFile() != null && !postRequest.getFile().isEmpty()) {
                String uploadedUrl = cloudinaryService.uploadFile(postRequest.getFile(), "folder_1");
                if (uploadedUrl == null) {
                    return ResponseEntity.badRequest().build();
                }
                post.setUrl(uploadedUrl);
            }

            postRepository.save(post);
            return ResponseEntity.ok().body(Map.of("message", "Post updated successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
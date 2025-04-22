// package com.skillshare.skill_platform.entity;


// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import org.springframework.data.annotation.Id;
// import org.springframework.data.mongodb.core.mapping.Document;

// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;

// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @Document(collection = "posts")
// public class Post {
//     @Id
//     private String id;
    
//     private String userId;
    
//     private String title;
    
//     private String description;
    
//     private PostType type; // SKILL_SHARE, LEARNING_UPDATE, LEARNING_PLAN
    
//     private List<MediaItem> mediaItems = new ArrayList<>();
    
//     private List<String> likes = new ArrayList<>();
    
//     private LocalDateTime createdAt;
    
//     private LocalDateTime updatedAt;
// }
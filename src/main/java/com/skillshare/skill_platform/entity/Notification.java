// package com.skillshare.skill_platform.entity;

// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import org.springframework.data.annotation.Id;
// import org.springframework.data.mongodb.core.mapping.Document;

// import java.time.LocalDateTime;

// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @Document(collection = "notifications")
// public class Notification {
//     @Id
//     private String id;
    
//     private String userId; // recipient
    
//     private String actorId; // user who performed the action
    
//     private String postId; // related post (if applicable)
    
//     private String commentId; // related comment (if applicable)
    
//     private NotificationType type;
    
//     private boolean read;
    
//     private LocalDateTime createdAt;
// }
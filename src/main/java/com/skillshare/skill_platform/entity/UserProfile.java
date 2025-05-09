package com.skillshare.skill_platform.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "user_profiles")
@Data
public class UserProfile {
    @Id
    private String id;
    private String userId;
    private String bio;
    private String profilePictureUrl;
    private String fullName;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime lastActiveAt = LocalDateTime.now();
}
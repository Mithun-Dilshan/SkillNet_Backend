package com.skillshare.skill_platform.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import com.skillshare.skill_platform.entity.Post;
import com.skillshare.skill_platform.entity.Comment;

@Data
public class UserProfileDTO {
    private String id;
    private String userId;
    private String bio;
    private String profilePictureUrl;
    private String fullName;
    
    // Activity stats
    private int totalPosts = 0;
    private int totalLikes = 0;
    private int totalComments = 0;
    
    // Follow counts
    private int followerCount = 0;
    private int followingCount = 0;
    private boolean isFollowing = false;
    
    // Activity timestamps
    private LocalDateTime createdAt;
    private LocalDateTime lastActiveAt;
    
    // User content
    private List<Post> userPosts;
    private List<Comment> userComments;
}
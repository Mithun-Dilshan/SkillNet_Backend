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
    
    private int totalPosts = 0;
    private int totalLikes = 0;
    private int totalComments = 0;
    
    private int followerCount = 0;
    private int followingCount = 0;
    private boolean isFollowing = false;
    
    private LocalDateTime createdAt;
    private LocalDateTime lastActiveAt;
    
    private List<Post> userPosts;
    private List<Comment> userComments;
}
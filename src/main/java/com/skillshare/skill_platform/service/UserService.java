package com.skillshare.skill_platform.service;

import com.skillshare.skill_platform.dto.UserDTO;
import com.skillshare.skill_platform.dto.UserProfileDTO;
import com.skillshare.skill_platform.entity.User;

import java.util.List;

public interface UserService {
    // Profile management
    UserProfileDTO createOrUpdateProfile(String userId, UserProfileDTO profileDTO);
    UserProfileDTO getProfile(String userId);
    
    // Simple user management methods
    User findOrCreateUserByEmail(String email);
    User findUserById(String userId);
    
    // Advanced user lookup
    User findUserByAnyIdentifier(String identifier);
    
    // Get all users
    List<UserProfileDTO> getAllUsers();
    
    // Follow/unfollow functionality
    boolean followUser(String followerId, String targetUserId);
    boolean unfollowUser(String followerId, String targetUserId);
    boolean isFollowing(String followerId, String targetUserId);
    List<String> getFollowers(String userId);
    List<String> getFollowing(String userId);
}
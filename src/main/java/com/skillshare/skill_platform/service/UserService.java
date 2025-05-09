package com.skillshare.skill_platform.service;

import com.skillshare.skill_platform.dto.UserDTO;
import com.skillshare.skill_platform.dto.UserProfileDTO;
import com.skillshare.skill_platform.entity.User;

import java.util.List;

public interface UserService {
    UserProfileDTO createOrUpdateProfile(String userId, UserProfileDTO profileDTO);
    UserProfileDTO getProfile(String userId);
    
    User findOrCreateUserByEmail(String email);
    User findUserById(String userId);
    
    User findUserByAnyIdentifier(String identifier);
    
    List<UserProfileDTO> getAllUsers();
    
    boolean followUser(String followerId, String targetUserId);
    boolean unfollowUser(String followerId, String targetUserId);
    boolean isFollowing(String followerId, String targetUserId);
    List<String> getFollowers(String userId);
    List<String> getFollowing(String userId);
}
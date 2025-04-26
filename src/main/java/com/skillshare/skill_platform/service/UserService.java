package com.skillshare.skill_platform.service;

import com.skillshare.skill_platform.dto.UserDTO;
import com.skillshare.skill_platform.dto.UserProfileDTO;
import com.skillshare.skill_platform.entity.User;

public interface UserService {
    // Profile management
    UserProfileDTO createOrUpdateProfile(String userId, UserProfileDTO profileDTO);
    UserProfileDTO getProfile(String userId);
    
    // Simple user management methods
    User findOrCreateUserByEmail(String email);
    User findUserById(String userId);
}
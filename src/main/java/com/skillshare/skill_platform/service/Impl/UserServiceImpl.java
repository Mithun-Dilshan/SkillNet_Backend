package com.skillshare.skill_platform.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skillshare.skill_platform.dto.UserDTO;
import com.skillshare.skill_platform.dto.UserProfileDTO;
import com.skillshare.skill_platform.entity.User;
import com.skillshare.skill_platform.entity.UserProfile;
import com.skillshare.skill_platform.repository.UserProfileRepository;
import com.skillshare.skill_platform.repository.UserRepository;
import com.skillshare.skill_platform.service.UserService;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;
    
    @Override
    public User findOrCreateUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setId(UUID.randomUUID().toString());
                    newUser.setEmail(email);
                    newUser.setName(email.split("@")[0]); // Use part of email as name
                    return userRepository.save(newUser);
                });
    }
    
    @Override
    public User findUserById(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public UserProfileDTO createOrUpdateProfile(String userId, UserProfileDTO profileDTO) {
        UserProfile profile = userProfileRepository.findByUserId(userId);
        if (profile == null) {
            profile = new UserProfile();
            profile.setId(UUID.randomUUID().toString());
            profile.setUserId(userId);
        }
        profile.setBio(profileDTO.getBio());
        profile.setProfilePictureUrl(profileDTO.getProfilePictureUrl());
        
        if (profileDTO.getFullName() != null) {
            profile.setFullName(profileDTO.getFullName());
        }
        
        userProfileRepository.save(profile);

        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setUserProfile(profile);
            userRepository.save(user);
        }

        UserProfileDTO result = new UserProfileDTO();
        result.setId(profile.getId());
        result.setUserId(profile.getUserId());
        result.setBio(profile.getBio());
        result.setProfilePictureUrl(profile.getProfilePictureUrl());
        result.setFullName(profile.getFullName());
        return result;
    }

    @Override
    public UserProfileDTO getProfile(String userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId);
        if (profile == null) {
            throw new RuntimeException("Profile not found");
        }
        UserProfileDTO result = new UserProfileDTO();
        result.setId(profile.getId());
        result.setUserId(profile.getUserId());
        result.setBio(profile.getBio());
        result.setProfilePictureUrl(profile.getProfilePictureUrl());
        result.setFullName(profile.getFullName());
        return result;
    }
}
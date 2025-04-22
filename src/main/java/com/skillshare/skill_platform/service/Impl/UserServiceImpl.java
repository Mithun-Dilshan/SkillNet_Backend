package com.skillshare.skill_platform.service.Impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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
    public UserDTO handleOAuthLogin(OAuth2AuthenticationToken authentication) {
        String email = authentication.getPrincipal().getAttribute("email");
        String name = authentication.getPrincipal().getAttribute("name");
        String provider = authentication.getAuthorizedClientRegistrationId();
        String oauthId = authentication.getPrincipal().getAttribute("sub");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setId(UUID.randomUUID().toString());
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setOauthProvider(provider);
                    newUser.setOauthId(oauthId);
                    return userRepository.save(newUser);
                });

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        return userDTO;
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
        userProfileRepository.save(profile);

        UserProfileDTO result = new UserProfileDTO();
        result.setId(profile.getId());
        result.setUserId(profile.getUserId());
        result.setBio(profile.getBio());
        result.setProfilePictureUrl(profile.getProfilePictureUrl());
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
        return result;
    }
}
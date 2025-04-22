package com.skillshare.skill_platform.service;



import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import com.skillshare.skill_platform.dto.UserDTO;
import com.skillshare.skill_platform.dto.UserProfileDTO;

public interface UserService {
    UserDTO handleOAuthLogin(OAuth2AuthenticationToken authentication);
    UserProfileDTO createOrUpdateProfile(String userId, UserProfileDTO profileDTO);
    UserProfileDTO getProfile(String userId);
}
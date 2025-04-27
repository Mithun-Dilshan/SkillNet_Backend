package com.skillshare.skill_platform.service;

import com.skillshare.skill_platform.dto.UserDTO;
import com.skillshare.skill_platform.entity.User;
import com.skillshare.skill_platform.entity.UserProfile;
import com.skillshare.skill_platform.repository.UserProfileRepository;
import com.skillshare.skill_platform.repository.UserRepository;
import com.skillshare.skill_platform.security.OAuth2UserInfo;
import com.skillshare.skill_platform.security.OAuth2UserInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserProfileRepository userProfileRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());
        
        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        
        if (userOptional.isPresent()) {
            user = userOptional.get();
            
            // Update user information if needed
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return createOAuth2User(user, oAuth2User.getAttributes(), oAuth2UserInfo.getName());
    }

    private OAuth2User createOAuth2User(User user, Map<String, Object> attributes, String nameAttributeKey) {
        return new DefaultOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
            attributes,
            "name" // Use the name attribute as the name attribute key
        );
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        try {
            System.out.println("Registering new user with email: " + oAuth2UserInfo.getEmail());
            
            // First create and save the User to get an ID
            User user = new User();
            user.setEmail(oAuth2UserInfo.getEmail());
            user.setOauthProvider(oAuth2UserRequest.getClientRegistration().getRegistrationId());
            user.setOauthId(oAuth2UserInfo.getId());
            user = userRepository.save(user);
            System.out.println("Created new user with ID: " + user.getId());
            
            // Then create and save UserProfile
            UserProfile userProfile = new UserProfile();
            userProfile.setUserId(user.getId());
            userProfile.setFullName(oAuth2UserInfo.getName());
            if (oAuth2UserInfo.getImageUrl() != null) {
                userProfile.setProfilePictureUrl(oAuth2UserInfo.getImageUrl());
            }
            userProfile = userProfileRepository.save(userProfile);
            System.out.println("Created UserProfile with ID: " + userProfile.getId());
            
            // Now set the saved UserProfile to User and save again
            user.setUserProfile(userProfile);
            return userRepository.save(user);
        } catch (Exception e) {
            System.err.println("Error registering new user: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        try {
            System.out.println("Updating existing user: " + existingUser.getId());
            
            UserProfile userProfile = existingUser.getUserProfile();
            if (userProfile == null) {
                System.out.println("Creating new UserProfile for existing user");
                userProfile = new UserProfile();
                userProfile.setUserId(existingUser.getId());
                
                // Save UserProfile first to get an ID
                userProfile = userProfileRepository.save(userProfile);
            }
            
            // Update profile properties
            userProfile.setFullName(oAuth2UserInfo.getName());
            if (oAuth2UserInfo.getImageUrl() != null) {
                userProfile.setProfilePictureUrl(oAuth2UserInfo.getImageUrl());
            }
            
            // Save the updated profile
            userProfile = userProfileRepository.save(userProfile);
            System.out.println("Updated UserProfile: " + userProfile.getId());
            
            // Set and save User
            existingUser.setUserProfile(userProfile);
            return userRepository.save(existingUser);
        } catch (Exception e) {
            System.err.println("Error updating existing user: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
} 
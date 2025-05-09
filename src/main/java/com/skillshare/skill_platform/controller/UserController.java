package com.skillshare.skill_platform.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.skillshare.skill_platform.dto.UserProfileDTO;
import com.skillshare.skill_platform.entity.User;
import com.skillshare.skill_platform.repository.UserRepository;
import com.skillshare.skill_platform.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDTO> createOrUpdateProfile(@PathVariable String userId, @RequestBody UserProfileDTO profileDTO) {
        logger.info("POST request to create/update profile for user: " + userId);
        
        try {
            UserProfileDTO result = userService.createOrUpdateProfile(userId, profileDTO);
            return ResponseEntity.status(201).body(result);
        } catch (Exception e) {
            logger.severe("Error creating/updating profile for user " + userId + ": " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating/updating profile: " + e.getMessage(), e);
        }
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDTO> getProfile(@PathVariable String userId) {
        logger.info("GET request for profile of user: " + userId);
        
        try {
            User user = userService.findUserByAnyIdentifier(userId);
            if (user == null) {
                logger.warning("User not found with identifier: " + userId);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with identifier: " + userId);
            }
            
            try {
                UserProfileDTO profile = userService.getProfile(userId);
                return ResponseEntity.ok(profile);
            } catch (Exception e) {
                logger.severe("Error getting profile for user " + userId + ": " + e.getMessage());
                UserProfileDTO minimalProfile = new UserProfileDTO();
                minimalProfile.setUserId(user.getId());
                minimalProfile.setFullName(user.getName());
                
                logger.info("Creating minimal profile for user: " + user.getId());
                return ResponseEntity.ok(userService.createOrUpdateProfile(user.getId(), minimalProfile));
            }
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            logger.severe("Unexpected error getting profile for user " + userId + ": " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving profile: " + e.getMessage(), e);
        }
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDTO> updateProfile(@PathVariable String userId, @RequestBody UserProfileDTO profileDTO) {
        logger.info("PUT request to update profile for user: " + userId);
        
        try {
            UserProfileDTO result = userService.createOrUpdateProfile(userId, profileDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.severe("Error updating profile for user " + userId + ": " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating profile: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/{userId}/profile")
    public ResponseEntity<Void> deleteProfile(@PathVariable String userId) {
        logger.info("DELETE request for profile of user: " + userId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search/{term}")
    public ResponseEntity<User> searchUserByTerm(@PathVariable String term) {
        logger.info("Searching for user with term: " + term);
        
        try {
            User user = userService.findUserByAnyIdentifier(term);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.severe("Error searching for user with term " + term + ": " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error searching for user: " + e.getMessage(), e);
        }
    }
    
    @GetMapping
    public ResponseEntity<java.util.List<UserProfileDTO>> getAllUsers(
        @RequestParam(required = false) String currentUserId
    ) {
        logger.info("GET request for all users. Current user: " + 
            (currentUserId != null ? currentUserId : "none"));
        
        try {
            java.util.List<UserProfileDTO> users = userService.getAllUsers();
            
            if (currentUserId != null && !currentUserId.isEmpty()) {
                User currentUser = userService.findUserByAnyIdentifier(currentUserId);
                
                if (currentUser != null) {
                    String actualCurrentUserId = currentUser.getId();
                    logger.info("Found current user: " + currentUserId + ", actual ID: " + actualCurrentUserId);
                    
                    List<String> followingList = currentUser.getFollowing();
                    if (followingList != null) {
                        Set<String> followingSet = new java.util.HashSet<>(followingList);
                        
                        for (UserProfileDTO user : users) {
                            boolean isFollowing = followingSet.contains(user.getUserId());
                            user.setFollowing(isFollowing);
                        }
                    } else {
                        for (UserProfileDTO user : users) {
                            user.setFollowing(false);
                        }
                    }
                } else {
                    logger.warning("Current user not found with identifier: " + currentUserId);
                }
            }
            
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.severe("Error getting all users: " + e.getMessage());
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error retrieving users: " + e.getMessage(), 
                e
            );
        }
    }
    
    @PostMapping("/{userId}/follow")
    public ResponseEntity<Object> followUser(@PathVariable String userId, @RequestParam String targetUserId) {
        logger.info("POST request for user " + userId + " to follow user " + targetUserId);
        
        try {
            User follower = userService.findUserByAnyIdentifier(userId);
            User target = userService.findUserByAnyIdentifier(targetUserId);
            
            if (follower == null) {
                logger.warning("Follower user not found with identifier: " + userId);
                return ResponseEntity.badRequest().body("Follower user not found");
            }
            
            if (target == null) {
                logger.warning("Target user not found with identifier: " + targetUserId);
                return ResponseEntity.badRequest().body("Target user not found");
            }
            
            boolean success = userService.followUser(follower.getId(), target.getId());
            
            if (success) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().body("Could not follow user");
            }
        } catch (Exception e) {
            logger.severe("Error following user: " + e.getMessage());
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error following user: " + e.getMessage(), 
                e
            );
        }
    }
    
    @PostMapping("/{userId}/unfollow")
    public ResponseEntity<Object> unfollowUser(@PathVariable String userId, @RequestParam String targetUserId) {
        logger.info("POST request for user " + userId + " to unfollow user " + targetUserId);
        
        try {
            User follower = userService.findUserByAnyIdentifier(userId);
            User target = userService.findUserByAnyIdentifier(targetUserId);
            
            if (follower == null) {
                logger.warning("Follower user not found with identifier: " + userId);
                return ResponseEntity.badRequest().body("Follower user not found");
            }
            
            if (target == null) {
                logger.warning("Target user not found with identifier: " + targetUserId);
                return ResponseEntity.badRequest().body("Target user not found");
            }
            
            boolean success = userService.unfollowUser(follower.getId(), target.getId());
            
            if (success) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().body("Could not unfollow user");
            }
        } catch (Exception e) {
            logger.severe("Error unfollowing user: " + e.getMessage());
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error unfollowing user: " + e.getMessage(), 
                e
            );
        }
    }

    @GetMapping("/{userId}/profile-with-status")
    public ResponseEntity<UserProfileDTO> getProfileWithFollowStatus(
        @PathVariable String userId,
        @RequestParam(required = false) String currentUserId
    ) {
        logger.info("GET request for profile of user: " + userId + " with follow status from: " + 
            (currentUserId != null ? currentUserId : "none"));
        
        try {
            User targetUser = userService.findUserByAnyIdentifier(userId);
            User currentUser = null;
            
            if (currentUserId != null && !currentUserId.isEmpty()) {
                currentUser = userService.findUserByAnyIdentifier(currentUserId);
            }
            
            if (targetUser == null) {
                logger.warning("Target user not found with identifier: " + userId);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with identifier: " + userId);
            }
            
            UserProfileDTO profile = userService.getProfile(targetUser.getId());
            
            if (currentUser != null) {
                if (currentUser.getFollowing() != null) {
                    boolean isFollowing = currentUser.getFollowing().contains(targetUser.getId());
                    profile.setFollowing(isFollowing);
                } else {
                    profile.setFollowing(false);
                }
            }
            
            return ResponseEntity.ok(profile);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            logger.severe("Unexpected error getting profile with status: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving profile: " + e.getMessage(), e);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable String userId) {
        logger.info("GET request for user ID: " + userId);
        
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Map<String, Object> userData = new HashMap<>();
                
                userData.put("id", user.getId());
                userData.put("name", user.getName());
                userData.put("email", user.getEmail());
                userData.put("oauthProvider", user.getOauthProvider());
                userData.put("oauthId", user.getOauthId());
                
                if (user.getUserProfile() != null) {
                    Map<String, Object> profileData = new HashMap<>();
                    profileData.put("id", user.getUserProfile().getId());
                    profileData.put("fullName", user.getUserProfile().getFullName());
                    profileData.put("profilePictureUrl", user.getUserProfile().getProfilePictureUrl());
                    profileData.put("bio", user.getUserProfile().getBio());
                    profileData.put("userId", user.getUserProfile().getUserId());
                    userData.put("userProfile", profileData);
                }
                
                userData.put("followers", user.getFollowers());
                userData.put("following", user.getFollowing());
                
                return ResponseEntity.ok(userData);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.severe("Error fetching user with ID " + userId + ": " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching user: " + e.getMessage(), e);
        }
    }
}

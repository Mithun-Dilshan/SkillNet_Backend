package com.skillshare.skill_platform.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.skillshare.skill_platform.dto.UserProfileDTO;
import com.skillshare.skill_platform.entity.User;
import com.skillshare.skill_platform.service.UserService;

import java.util.logging.Logger;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    @Autowired
    private UserService userService;

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
            // First try to find the user
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
                // If the user exists but the profile doesn't, create a minimal profile
                UserProfileDTO minimalProfile = new UserProfileDTO();
                minimalProfile.setUserId(user.getId());
                minimalProfile.setFullName(user.getName());
                
                // Create the profile in the database
                logger.info("Creating minimal profile for user: " + user.getId());
                return ResponseEntity.ok(userService.createOrUpdateProfile(user.getId(), minimalProfile));
            }
        } catch (ResponseStatusException rse) {
            // Rethrow ResponseStatusException
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
        // Profile deletion is not implemented yet, but returns success for API compatibility
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
            
            // If currentUserId is provided, check follow status for each user
            if (currentUserId != null && !currentUserId.isEmpty()) {
                // First find the actual user by any identifier
                User currentUser = userService.findUserByAnyIdentifier(currentUserId);
                
                if (currentUser != null) {
                    String actualCurrentUserId = currentUser.getId();
                    logger.info("Found current user: " + currentUserId + ", actual ID: " + actualCurrentUserId);
                    
                    // Get the user's following list once (more efficient)
                    List<String> followingList = currentUser.getFollowing();
                    if (followingList != null) {
                        // Create a set for faster lookups
                        Set<String> followingSet = new java.util.HashSet<>(followingList);
                        
                        // Check following status for all users at once
                        for (UserProfileDTO user : users) {
                            boolean isFollowing = followingSet.contains(user.getUserId());
                            user.setFollowing(isFollowing);
                        }
                    } else {
                        // If following list is null, no users are followed
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
            // First, find the actual user objects by any identifier (could be username or ID)
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
            
            // Use actual MongoDB IDs for the follow operation
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
            // First, find the actual user objects by any identifier (could be username or ID)
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
            
            // Use actual MongoDB IDs for the unfollow operation
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
            // First try to find both users
            User targetUser = userService.findUserByAnyIdentifier(userId);
            User currentUser = null;
            
            if (currentUserId != null && !currentUserId.isEmpty()) {
                currentUser = userService.findUserByAnyIdentifier(currentUserId);
            }
            
            if (targetUser == null) {
                logger.warning("Target user not found with identifier: " + userId);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with identifier: " + userId);
            }
            
            // Get the profile
            UserProfileDTO profile = userService.getProfile(targetUser.getId());
            
            // Check follow status if we have a current user
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
            // Rethrow ResponseStatusException
            throw rse;
        } catch (Exception e) {
            logger.severe("Unexpected error getting profile with status: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving profile: " + e.getMessage(), e);
        }
    }
}

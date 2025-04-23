package com.skillshare.skill_platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.skillshare.skill_platform.dto.UserProfileDTO;
import com.skillshare.skill_platform.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDTO> createOrUpdateProfile(@PathVariable String userId, @RequestBody UserProfileDTO profileDTO) {
        // Optional: Add authorization check to ensure users can only modify their own profiles
        // checkUserAuthorization(userId);
        
        UserProfileDTO result = userService.createOrUpdateProfile(userId, profileDTO);
        return ResponseEntity.status(201).body(result);
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDTO> getProfile(@PathVariable String userId) {
        UserProfileDTO profile = userService.getProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDTO> updateProfile(@PathVariable String userId, @RequestBody UserProfileDTO profileDTO) {
        // Optional: Add authorization check to ensure users can only modify their own profiles
        // checkUserAuthorization(userId);
        
        UserProfileDTO result = userService.createOrUpdateProfile(userId, profileDTO);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{userId}/profile")
    public ResponseEntity<Void> deleteProfile(@PathVariable String userId) {
        // Optional: Add authorization check to ensure users can only delete their own profiles
        // checkUserAuthorization(userId);
        
        userService.deleteProfile(userId);
        return ResponseEntity.noContent().build();
    }
    
    // Helper method to check if current user is authorized to access the requested profile
    private void checkUserAuthorization(String userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Implementation depends on how you store the userId in the authentication object
        // This is just a placeholder for the concept
        String currentUserId = auth.getName(); // or extract from Principal
        
        if (!userId.equals(currentUserId)) {
            throw new RuntimeException("Unauthorized access to profile");
        }
    }
}
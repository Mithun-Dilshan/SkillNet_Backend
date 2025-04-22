package com.skillshare.skill_platform.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
        UserProfileDTO result = userService.createOrUpdateProfile(userId, profileDTO);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{userId}/profile")
    public ResponseEntity<Void> deleteProfile(@PathVariable String userId) {
        // Implement delete logic if required
        return ResponseEntity.noContent().build();
    }
}

package com.skillshare.skill_platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.skillshare.skill_platform.entity.User;
import com.skillshare.skill_platform.entity.UserProfile;
import com.skillshare.skill_platform.repository.UserRepository;
import com.skillshare.skill_platform.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        
        System.out.println("Login request received for email: " + email);
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        Map<String, Object> response = new HashMap<>();
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            String token = UUID.randomUUID().toString();
            
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("token", token);
            
            // Create a user map with all user fields for the response
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("name", user.getName());
            userMap.put("email", user.getEmail());
            userMap.put("oauthProvider", user.getOauthProvider());
            userMap.put("oauthId", user.getOauthId());
            
            // Add profile information if available
            if (user.getUserProfile() != null) {
                UserProfile profile = user.getUserProfile();
                userMap.put("userProfile", profile);
            }
            
            // Add followers and following information
            userMap.put("followers", user.getFollowers());
            userMap.put("following", user.getFollowing());
            
            response.put("user", userMap);
            
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Invalid credentials");
            return ResponseEntity.status(401).body(response);
        }
    }
    

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> checkAuthStatus(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> response = new HashMap<>();
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            response.put("authenticated", true);
            return ResponseEntity.ok(response);
        }
        
        response.put("authenticated", false);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> registerRequest) {
        String name = registerRequest.get("name");
        String email = registerRequest.get("email");
        
        System.out.println("Register request received for email: " + email);
        
        Optional<User> existingUser = userRepository.findByEmail(email);
        Map<String, Object> response = new HashMap<>();
        
        if (existingUser.isPresent()) {
            response.put("success", false);
            response.put("message", "User with this email already exists");
            return ResponseEntity.badRequest().body(response);
        }
        
        User newUser = userService.findOrCreateUserByEmail(email);
        newUser.setName(name);
        userRepository.save(newUser);
        
        String token = UUID.randomUUID().toString();
        
        response.put("success", true);
        response.put("message", "Registration successful");
        response.put("token", token);
        
        // Create a user map with all user fields for the response
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", newUser.getId());
        userMap.put("name", newUser.getName());
        userMap.put("email", newUser.getEmail());
        userMap.put("oauthProvider", newUser.getOauthProvider());
        userMap.put("oauthId", newUser.getOauthId());
        
        // Add profile information if available
        if (newUser.getUserProfile() != null) {
            UserProfile profile = newUser.getUserProfile();
            userMap.put("userProfile", profile);
        }
        
        // Add followers and following information
        userMap.put("followers", newUser.getFollowers());
        userMap.put("following", newUser.getFollowing());
        
        response.put("user", userMap);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }
}

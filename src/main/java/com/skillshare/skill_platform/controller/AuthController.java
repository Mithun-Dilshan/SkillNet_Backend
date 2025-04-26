package com.skillshare.skill_platform.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillshare.skill_platform.entity.User;
import com.skillshare.skill_platform.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;

    /**
     * Simple login endpoint for development
     * Just provide an email (any password will work)
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        
        // Simple login for development - no password check
        User user = userService.findOrCreateUserByEmail(email);
        
        // Create response with user info
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("success", true);
        
        return ResponseEntity.ok(response);
    }
}

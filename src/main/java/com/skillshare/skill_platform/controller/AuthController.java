package com.skillshare.skill_platform.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillshare.skill_platform.dto.UserDTO;
import com.skillshare.skill_platform.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/success")
    public ResponseEntity<UserDTO> loginSuccess(OAuth2AuthenticationToken authentication) {
        UserDTO userDTO = userService.handleOAuthLogin(authentication);
        return ResponseEntity.ok(userDTO);
    }
}

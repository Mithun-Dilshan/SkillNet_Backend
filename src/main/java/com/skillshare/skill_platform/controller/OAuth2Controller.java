package com.skillshare.skill_platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/oauth2")
public class OAuth2Controller {

    @Value("${app.oauth2.authorizedRedirectUris}")
    private String[] authorizedRedirectUris;
    

    private static final Map<String, String> codeToTokenMap = new HashMap<>();

    @GetMapping("/success")
    public RedirectView success(@AuthenticationPrincipal OAuth2User oauth2User) {
        String frontendRedirectUrl = authorizedRedirectUris[0]; // Use the first authorized redirect URI
        

        System.out.println("OAuth2 success endpoint called!");
        System.out.println("OAuth2 success! User: " + (oauth2User != null ? oauth2User.getName() : "null"));
        if (oauth2User != null) {
            System.out.println("User attributes: " + oauth2User.getAttributes());
        } else {
            System.out.println("Warning: oauth2User is null");
        }
        
        try {
            String token = generateToken(oauth2User);
            

            frontendRedirectUrl += "?token=" + token;
        } catch (Exception e) {
            System.err.println("Error generating token: " + e.getMessage());
            e.printStackTrace();
            frontendRedirectUrl += "?error=Authentication failed: " + e.getMessage();
        }
        
        System.out.println("Redirecting to: " + frontendRedirectUrl);
        return new RedirectView(frontendRedirectUrl);
    }

    @GetMapping("/error")
    public RedirectView error(@RequestParam(required = false) String error) {
        String frontendRedirectUrl = authorizedRedirectUris[0] + "?error=" + 
            (error != null ? error : "Authentication failed");
        System.out.println("OAuth error endpoint called. Redirecting to: " + frontendRedirectUrl);
        return new RedirectView(frontendRedirectUrl);
    }

    @GetMapping("/user")
    public Map<String, Object> getUser(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            System.out.println("User endpoint called but oauth2User is null");
            return new HashMap<>();
        }
        System.out.println("User endpoint called for: " + oauth2User.getName());
        return oauth2User.getAttributes();
    }
    
    @GetMapping("/token")
    public ResponseEntity<Map<String, String>> exchangeToken(@RequestParam("code") String code) {
        Map<String, String> response = new HashMap<>();
        
        System.out.println("Token endpoint called with code: " + code);
        

        String token = codeToTokenMap.get(code);
        if (token == null) {
            token = UUID.randomUUID().toString();
            codeToTokenMap.put(code, token);
        }
        
        response.put("token", token);
        System.out.println("Returning token: " + token);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/debug")
    public ResponseEntity<Map<String, Object>> debug() {
        Map<String, Object> debug = new HashMap<>();
        debug.put("authorizedRedirectUris", authorizedRedirectUris);
        debug.put("serverStatus", "running");
        debug.put("tokens", codeToTokenMap.size());
        return ResponseEntity.ok(debug);
    }
    
    private String generateToken(OAuth2User oauth2User) {
        return UUID.randomUUID().toString();
    }
} 
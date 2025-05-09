package com.skillshare.skill_platform.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.skillshare.skill_platform.entity.User;
import com.skillshare.skill_platform.repository.UserRepository;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${app.oauth2.authorizedRedirectUris}")
    private String[] authorizedRedirectUris;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                        Authentication authentication) throws IOException, ServletException {
        
        try {
            System.out.println("=== OAuth2 Authentication Success Handler ===");
            
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
            response.setHeader("Access-Control-Expose-Headers", "Authorization, Content-Type, Access-Control-Allow-Origin");
            
            String token = UUID.randomUUID().toString();
            
            System.out.println("OAuth2 Authentication Success for user: " + authentication.getName());
            
            Object principal = authentication.getPrincipal();
            System.out.println("Authentication principal type: " + (principal != null ? principal.getClass().getName() : "null"));
            String userId = null;
            String userDisplayName = null;
            String email = null;
            String profilePictureUrl = null;
            
            if (principal instanceof OAuth2User) {
                OAuth2User oauth2User = (OAuth2User) principal;
                System.out.println("OAuth2User attributes: " + oauth2User.getAttributes());
                System.out.println("OAuth2User name: " + oauth2User.getName());
                
                userDisplayName = oauth2User.getName();
                email = oauth2User.getAttribute("email");
                profilePictureUrl = oauth2User.getAttribute("picture");
                
                // Try to get actual user ID from the database
                if (email != null) {
                    Optional<User> userOpt = userRepository.findByEmail(email);
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        userId = user.getId();
                        userDisplayName = user.getName();
                        
                        // Get profile picture from user profile if available
                        if (user.getUserProfile() != null && user.getUserProfile().getProfilePictureUrl() != null) {
                            profilePictureUrl = user.getUserProfile().getProfilePictureUrl();
                        }
                    }
                }
                
                // If we couldn't find the user in the database, use the OAuth user name as a fallback
                if (userId == null) {
                    userId = oauth2User.getName();
                }
                
                oauth2User.getAttributes().forEach((key, value) -> {
                    System.out.println("  Attribute: " + key + " = " + value + (value == null ? " (NULL)" : ""));
                });
            }
            
            // URL encode the userDisplayName to handle special characters
            String encodedUserName = URLEncoder.encode(userDisplayName, StandardCharsets.UTF_8.toString());
            String encodedEmail = email != null ? URLEncoder.encode(email, StandardCharsets.UTF_8.toString()) : null;
            String encodedPictureUrl = profilePictureUrl != null ? URLEncoder.encode(profilePictureUrl, StandardCharsets.UTF_8.toString()) : null;
            
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(authorizedRedirectUris[0])
                    .queryParam("token", token)
                    .queryParam("userId", userId)
                    .queryParam("userName", encodedUserName);
            
            // Add email and profile picture if available
            if (encodedEmail != null) {
                uriBuilder.queryParam("email", encodedEmail);
            }
            
            if (encodedPictureUrl != null) {
                uriBuilder.queryParam("profilePictureUrl", encodedPictureUrl);
            }
            
            String redirectUrl = uriBuilder.build().toUriString();
            
            System.out.println("Redirecting to: " + redirectUrl);
            
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        } catch (Exception e) {
            System.err.println("Error in OAuth2 authentication success handler: " + e.getMessage());
            e.printStackTrace();
            
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
            response.setHeader("Access-Control-Expose-Headers", "Authorization, Content-Type, Access-Control-Allow-Origin");
            
            try {
                if (e.getMessage() != null && e.getMessage().contains("NULL id")) {
                    System.err.println("MongoDB NULL id reference error detected. This typically happens when trying to reference an entity with a null ID.");
                    System.err.println("Check User and UserProfile entity creation/updates in CustomOAuth2UserService.");
                }
                
                String redirectUrl = UriComponentsBuilder.fromUriString(authorizedRedirectUris[0])
                        .queryParam("error", "Authentication failed: " + e.getMessage())
                        .build().toUriString();
                
                getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            } catch (Exception ex) {
                System.err.println("Critical error in error handler: " + ex.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication failed");
            }
        }
    }
} 
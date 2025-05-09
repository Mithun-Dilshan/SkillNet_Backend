package com.skillshare.skill_platform.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skillshare.skill_platform.dto.UserDTO;
import com.skillshare.skill_platform.dto.UserProfileDTO;
import com.skillshare.skill_platform.entity.Post;
import com.skillshare.skill_platform.entity.User;
import com.skillshare.skill_platform.entity.UserProfile;
import com.skillshare.skill_platform.entity.Comment;
import com.skillshare.skill_platform.repository.PostRepository;
import com.skillshare.skill_platform.repository.UserProfileRepository;
import com.skillshare.skill_platform.repository.UserRepository;
import com.skillshare.skill_platform.repository.CommentRepository;
import com.skillshare.skill_platform.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private CommentRepository commentRepository;

    @Override
    public User findOrCreateUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setId(UUID.randomUUID().toString());
                    newUser.setEmail(email);
                    newUser.setName(email.split("@")[0]); 
                    return userRepository.save(newUser);
                });
    }
    
    @Override
    public User findUserById(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    /**
     * Finds a user by their name, email, or ID
     * @param identifier The identifier to search by (name, email, or ID)
     * @return The User if found, null otherwise
     */
    @Override
    public User findUserByAnyIdentifier(String identifier) {
        logger.info("Finding user by identifier: " + identifier);
        
        Optional<User> userById = userRepository.findById(identifier);
        if (userById.isPresent()) {
            logger.info("Found user by ID: " + identifier);
            return userById.get();
        }
        
        Optional<User> userByEmail = userRepository.findByEmail(identifier);
        if (userByEmail.isPresent()) {
            logger.info("Found user by email: " + identifier);
            return userByEmail.get();
        }
        
        Optional<User> userByName = userRepository.findByName(identifier);
        if (userByName.isPresent()) {
            logger.info("Found user by exact name: " + identifier);
            return userByName.get();
        }
        
        if (identifier.contains(".")) {
            String normalizedName = identifier.replace('.', ' ');
            logger.info("Trying with normalized name (dots replaced with spaces): " + normalizedName);
            userByName = userRepository.findByName(normalizedName);
            if (userByName.isPresent()) {
                logger.info("Found user by normalized name: " + normalizedName);
                return userByName.get();
            }
        }
        
        List<User> usersByPartialName = userRepository.findByNameContainingIgnoreCase(identifier);
        if (!usersByPartialName.isEmpty()) {
            if (usersByPartialName.size() > 1) {
                logger.info("Found " + usersByPartialName.size() + " users by partial name match, finding best match");
                
                for (User user : usersByPartialName) {
                    if (user.getName().toLowerCase().contains(identifier.toLowerCase())) {
                        logger.info("Found best match by substring: " + user.getName());
                        return user;
                    }
                }
                
                logger.info("Returning first match from partial results: " + usersByPartialName.get(0).getName());
            } else {
                logger.info("Found user by partial name match: " + usersByPartialName.get(0).getName());
            }
            return usersByPartialName.get(0);
        }
        
        logger.warning("No user found for identifier: " + identifier);
        return null;
    }

    @Override
    public UserProfileDTO createOrUpdateProfile(String userId, UserProfileDTO profileDTO) {
        logger.info("Creating or updating profile for user: " + userId);
        
        User user = findUserByAnyIdentifier(userId);
        
        if (user == null) {
            logger.warning("User not found for ID: " + userId);
            throw new RuntimeException("User not found for ID: " + userId);
        }
        
        String actualUserId = user.getId();
        logger.info("Using actual user ID: " + actualUserId);
        
        UserProfile profile = null;
        
        if (user.getUserProfile() != null) {
            logger.info("User has profile reference: " + user.getUserProfile().getId());
            profile = user.getUserProfile();
        } else {
            profile = userProfileRepository.findByUserId(actualUserId);
        }
        
        if (profile == null) {
            logger.info("Creating new profile for user: " + actualUserId);
            profile = new UserProfile();
            profile.setId(UUID.randomUUID().toString());
            profile.setUserId(actualUserId);
            profile.setCreatedAt(java.time.LocalDateTime.now());
        } else {
            logger.info("Updating existing profile: " + profile.getId());
        }
        
        profile.setBio(profileDTO.getBio());
        profile.setProfilePictureUrl(profileDTO.getProfilePictureUrl());
        
        if (profileDTO.getFullName() != null) {
            profile.setFullName(profileDTO.getFullName());
        }
        
        profile.setLastActiveAt(java.time.LocalDateTime.now());
        
        Map<String, Object> userContent = getUserContent(actualUserId);
        
        profile = userProfileRepository.save(profile);
        logger.info("Saved profile with ID: " + profile.getId());

        user.setUserProfile(profile);
        userRepository.save(user);
        logger.info("Updated user reference to profile");

        UserProfileDTO result = new UserProfileDTO();
        result.setId(profile.getId());
        result.setUserId(profile.getUserId());
        result.setBio(profile.getBio());
        result.setProfilePictureUrl(profile.getProfilePictureUrl());
        result.setFullName(profile.getFullName());
        result.setTotalPosts((int) userContent.get("totalPosts"));
        result.setTotalLikes((int) userContent.get("totalLikes"));
        result.setTotalComments((int) userContent.get("totalComments"));
        result.setCreatedAt(profile.getCreatedAt());
        result.setLastActiveAt(profile.getLastActiveAt());
        
        result.setUserPosts((List<Post>) userContent.get("posts"));
        result.setUserComments((List<Comment>) userContent.get("comments"));
        
        logger.info(String.format("Returning profile with stats: posts=%d, likes=%d, comments=%d",
            result.getTotalPosts(), result.getTotalLikes(), result.getTotalComments()));
            
        return result;
    }

    /**
     * Gets a user's posts and comments
     * @param userId The user ID to find posts and comments for
     * @return A map containing the user's posts and comments
     */
    public Map<String, Object> getUserContent(String userId) {
        logger.info("Getting content for user: " + userId);
        Map<String, Object> content = new HashMap<>();
        
        try {
            List<Post> userPosts = postRepository.findByUserId(userId);
            logger.info("Found " + userPosts.size() + " posts for user " + userId);
            content.put("posts", userPosts);
            
            List<Comment> userComments = commentRepository.findByUserId(userId);
            logger.info("Found " + userComments.size() + " comments made by user " + userId);
            content.put("comments", userComments);
            
            content.put("totalPosts", userPosts.size());
            
            int totalLikes = 0;
            for (Post post : userPosts) {
                if (post.getLikes() != null) {
                    totalLikes += post.getLikes().size();
                }
            }
            content.put("totalLikes", totalLikes);
            content.put("totalComments", userComments.size());
            
        } catch (Exception e) {
            logger.warning("Error retrieving user content: " + e.getMessage());
            e.printStackTrace();
        }
        
        return content;
    }

    @Override
    public UserProfileDTO getProfile(String userId) {
        logger.info("Getting profile for user: " + userId);
        
        User user = findUserByAnyIdentifier(userId);
        
        if (user != null) {
            logger.info("Found user by identifier: " + userId + ", actual ID: " + user.getId());
            
            if (user.getUserProfile() != null) {
                logger.info("Using profile from user reference: " + user.getUserProfile().getId());
                UserProfile profile = user.getUserProfile();
                
                Map<String, Object> userContent = getUserContent(user.getId());
                
                profile.setLastActiveAt(java.time.LocalDateTime.now());
                userProfileRepository.save(profile);
                
                UserProfileDTO result = new UserProfileDTO();
                result.setId(profile.getId());
                result.setUserId(profile.getUserId());
                result.setBio(profile.getBio());
                result.setProfilePictureUrl(profile.getProfilePictureUrl());
                result.setFullName(profile.getFullName());
                result.setTotalPosts((int) userContent.get("totalPosts"));
                result.setTotalLikes((int) userContent.get("totalLikes"));
                result.setTotalComments((int) userContent.get("totalComments"));
                result.setCreatedAt(profile.getCreatedAt());
                result.setLastActiveAt(profile.getLastActiveAt());
                
                result.setFollowerCount(user.getFollowers().size());
                result.setFollowingCount(user.getFollowing().size());
                
                result.setUserPosts((List<Post>) userContent.get("posts"));
                result.setUserComments((List<Comment>) userContent.get("comments"));
                
                logger.info(String.format("Returning profile with stats: posts=%d, likes=%d, comments=%d, followers=%d, following=%d",
                    result.getTotalPosts(), result.getTotalLikes(), result.getTotalComments(), 
                    result.getFollowerCount(), result.getFollowingCount()));
                    
                return result;
            }
            
            userId = user.getId();
        }
        
        UserProfile profile = userProfileRepository.findByUserId(userId);
        if (profile == null) {
            logger.warning("Profile not found for user: " + userId);
            
            if (user != null) {
                logger.info("Creating new profile for user: " + user.getId());
                profile = new UserProfile();
                profile.setId(UUID.randomUUID().toString());
                profile.setUserId(user.getId());
                profile.setCreatedAt(java.time.LocalDateTime.now());
                profile.setLastActiveAt(java.time.LocalDateTime.now());
                
                if (user.getName() != null) {
                    profile.setFullName(user.getName());
                }
                
                profile = userProfileRepository.save(profile);
                
                user.setUserProfile(profile);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Profile not found");
            }
        }
        
        String actualUserId = profile.getUserId();
        
        User profileUser = userRepository.findById(actualUserId).orElse(null);
        
        Map<String, Object> userContent = getUserContent(actualUserId);
        
        profile.setLastActiveAt(java.time.LocalDateTime.now());
        userProfileRepository.save(profile);
        
        logger.info("Found profile: " + profile.getId());
        UserProfileDTO result = new UserProfileDTO();
        result.setId(profile.getId());
        result.setUserId(profile.getUserId());
        result.setBio(profile.getBio());
        result.setProfilePictureUrl(profile.getProfilePictureUrl());
        result.setFullName(profile.getFullName());
        result.setTotalPosts((int) userContent.get("totalPosts"));
        result.setTotalLikes((int) userContent.get("totalLikes"));
        result.setTotalComments((int) userContent.get("totalComments"));
        result.setCreatedAt(profile.getCreatedAt());
        result.setLastActiveAt(profile.getLastActiveAt());
        
        if (profileUser != null) {
            result.setFollowerCount(profileUser.getFollowers().size());
            result.setFollowingCount(profileUser.getFollowing().size());
        }
        
        result.setUserPosts((List<Post>) userContent.get("posts"));
        result.setUserComments((List<Comment>) userContent.get("comments"));
        
        logger.info(String.format("Returning profile with stats: posts=%d, likes=%d, comments=%d, followers=%d, following=%d",
            result.getTotalPosts(), result.getTotalLikes(), result.getTotalComments(), 
            result.getFollowerCount(), result.getFollowingCount()));
            
        return result;
    }

    /**
     * Gets a profile with information about the current user's follow status
     * @param userId The user ID to get the profile for
     * @param currentUserId The ID of the requesting user (to check follow status)
     * @return The user profile DTO
     */
    public UserProfileDTO getProfileWithFollowStatus(String userId, String currentUserId) {
        // Get the base profile
        UserProfileDTO profile = getProfile(userId);
        
        // If we have a current user ID, check follow status
        if (currentUserId != null && !currentUserId.isEmpty() && !currentUserId.equals(userId)) {
            User currentUser = findUserById(currentUserId);
            if (currentUser != null && currentUser.getFollowing() != null) {
                boolean isFollowing = currentUser.getFollowing().contains(userId);
                profile.setFollowing(isFollowing);
            }
        }
        
        return profile;
    }

    @Override
    public List<UserProfileDTO> getAllUsers() {
        logger.info("Fetching all users");
        
        try {
            List<User> users = userRepository.findAll();
            
            Map<String, UserProfile> userProfiles = new HashMap<>();
            List<String> userIds = users.stream().map(User::getId).toList();
            
            List<UserProfileDTO> result = new ArrayList<>();
            
            for (User user : users) {
                UserProfileDTO profileDTO = new UserProfileDTO();
                profileDTO.setUserId(user.getId());
                profileDTO.setFullName(user.getName());
                
                if (user.getUserProfile() != null) {
                    UserProfile profile = user.getUserProfile();
                    profileDTO.setId(profile.getId());
                    profileDTO.setBio(profile.getBio());
                    profileDTO.setProfilePictureUrl(profile.getProfilePictureUrl());
                    profileDTO.setCreatedAt(profile.getCreatedAt());
                    profileDTO.setLastActiveAt(profile.getLastActiveAt());
                }
                
                if (user.getFollowers() != null) {
                    profileDTO.setFollowerCount(user.getFollowers().size());
                }
                
                if (user.getFollowing() != null) {
                    profileDTO.setFollowingCount(user.getFollowing().size());
                }
                
                
                try {
                    Map<String, Object> userContent = new HashMap<>();
                    
                    long postCount = postRepository.countByUserId(user.getId());
                    long commentCount = commentRepository.countByUserId(user.getId());
                    
                    profileDTO.setTotalPosts((int) postCount);
                    profileDTO.setTotalComments((int) commentCount);
                    
                    profileDTO.setTotalLikes(0);
                } catch (Exception e) {
                    logger.warning("Error getting content counts for user " + user.getId() + ": " + e.getMessage());
                }
                
                result.add(profileDTO);
            }
            
            return result;
        } catch (Exception e) {
            logger.severe("Error in getAllUsers: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public boolean followUser(String followerId, String targetUserId) {
        logger.info("User " + followerId + " following user " + targetUserId);
        
        if (followerId == null || targetUserId == null || followerId.equals(targetUserId)) {
            logger.warning("Invalid follow request: follower=" + followerId + ", target=" + targetUserId);
            return false;
        }
        
        try {
            User follower = findUserById(followerId);
            User target = findUserById(targetUserId);
            
            if (follower == null || target == null) {
                logger.warning("Cannot follow: User not found");
                return false;
            }
            
            if (follower.getFollowing() == null) {
                follower.setFollowing(new ArrayList<>());
            }
            
            if (target.getFollowers() == null) {
                target.setFollowers(new ArrayList<>());
            }
            
            if (follower.getFollowing().contains(targetUserId)) {
                logger.info("Already following this user");
                return true; 
            }
            
            follower.getFollowing().add(targetUserId);
            userRepository.save(follower);
            
            // Update target's followers list
            target.getFollowers().add(followerId);
            userRepository.save(target);
            
            logger.info("Follow successful");
            return true;
        } catch (Exception e) {
            logger.severe("Error following user: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean unfollowUser(String followerId, String targetUserId) {
        logger.info("User " + followerId + " unfollowing user " + targetUserId);
        
        // Check if followerId and targetUserId are valid and not the same
        if (followerId == null || targetUserId == null || followerId.equals(targetUserId)) {
            logger.warning("Invalid unfollow request: follower=" + followerId + ", target=" + targetUserId);
            return false;
        }
        
        try {
            User follower = findUserById(followerId);
            User target = findUserById(targetUserId);
            
            if (follower == null || target == null) {
                logger.warning("Cannot unfollow: User not found");
                return false;
            }
            
            if (follower.getFollowing() == null) {
                follower.setFollowing(new ArrayList<>());
                return true; 
            }
            
            if (target.getFollowers() == null) {
                target.setFollowers(new ArrayList<>());
                return true; 
            }
            
            if (!follower.getFollowing().contains(targetUserId)) {
                logger.info("Not following this user");
                return true; 
            }
            
            follower.getFollowing().remove(targetUserId);
            userRepository.save(follower);
            
            target.getFollowers().remove(followerId);
            userRepository.save(target);
            
            logger.info("Unfollow successful");
            return true;
        } catch (Exception e) {
            logger.severe("Error unfollowing user: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean isFollowing(String followerId, String targetUserId) {
        if (followerId == null || targetUserId == null || followerId.equals(targetUserId)) {
            return false;
        }
        
        try {
            User follower = findUserById(followerId);
            
            if (follower == null) {
                return false;
            }
            
            if (follower.getFollowing() == null) {
                return false;
            }
            
            return follower.getFollowing().contains(targetUserId);
        } catch (Exception e) {
            logger.severe("Error checking follow status: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public List<String> getFollowers(String userId) {
        try {
            User user = findUserById(userId);
            
            if (user == null) {
                return List.of();
            }
            
            if (user.getFollowers() == null) {
                return List.of();
            }
            
            return user.getFollowers();
        } catch (Exception e) {
            logger.severe("Error getting followers: " + e.getMessage());
            return List.of();
        }
    }
    
    @Override
    public List<String> getFollowing(String userId) {
        try {
            User user = findUserById(userId);
            
            if (user == null) {
                return List.of();
            }
            
            if (user.getFollowing() == null) {
                return List.of();
            }
            
            return user.getFollowing();
        } catch (Exception e) {
            logger.severe("Error getting following: " + e.getMessage());
            return List.of();
        }
    }
}
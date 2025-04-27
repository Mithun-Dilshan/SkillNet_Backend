package com.skillshare.skill_platform.service.Impl;

import com.skillshare.skill_platform.dto.LearningPlanRQ;
import com.skillshare.skill_platform.dto.LearningPlanResponse;
import com.skillshare.skill_platform.dto.UserDTO;
import com.skillshare.skill_platform.entity.LearningPlan;
import com.skillshare.skill_platform.entity.Topic;
import com.skillshare.skill_platform.entity.User;
import com.skillshare.skill_platform.entity.UserProfile;
import com.skillshare.skill_platform.exception.ResourceNotFoundException;
import com.skillshare.skill_platform.repository.LearningPlanRepository;
import com.skillshare.skill_platform.repository.UserRepository;
import com.skillshare.skill_platform.service.LearningPlanService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LearningPlanServiceImpl implements LearningPlanService {

  private final LearningPlanRepository learningPlanRepository;
  private final UserRepository userRepository;

  @Override
  public LearningPlan create(LearningPlanRQ rq, String userId) {
    LearningPlan learningPlan = new LearningPlan();
    BeanUtils.copyProperties(rq, learningPlan);
    learningPlan.setCreatedAt(LocalDateTime.now());
    learningPlan.setUserId(userId);
    learningPlan.setFollowers(0);
    return learningPlanRepository.save(learningPlan);
  }

  @Override
  public List<LearningPlanResponse> getAll() {
    try {
      System.out.println("Starting to fetch all learning plans...");
      
      List<LearningPlan> learningPlans = learningPlanRepository.findAll();
      System.out.println("Database query completed. Found " + learningPlans.size() + " learning plans");
      
      if (learningPlans.isEmpty()) {
        System.out.println("No learning plans found in database");
        return new ArrayList<>();
      }
      
      System.out.println("Mapping learning plans to response DTOs...");
      List<LearningPlanResponse> responses = new ArrayList<>();
      
      for (LearningPlan plan : learningPlans) {
        try {
          LearningPlanResponse response = mapToResponse(plan);
          responses.add(response);
        } catch (Exception e) {
          System.err.println("Error mapping learning plan with ID " + plan.getId() + ": " + e.getMessage());
        }
      }
      
      System.out.println("Successfully mapped " + responses.size() + " learning plans");
      return responses;
      
    } catch (Exception e) {
      System.err.println("Critical error fetching all learning plans: " + e.getMessage());
      e.printStackTrace();
      throw new RuntimeException("Failed to retrieve learning plans", e);
    }
  }

  @Override
  public List<LearningPlanResponse> getById(String userId) {
    List<LearningPlan> learningPlans = learningPlanRepository.findByUserId(userId);

    if (learningPlans.isEmpty()) {
      throw new ResourceNotFoundException("Not found learning plans for user with id: " + userId);
    }

    return learningPlans.stream()
        .map(this::mapToResponse)
        .toList();
  }
  
  @Override
  public LearningPlanResponse getByPlanId(String planId) {
    try {
      System.out.println("Service: Fetching learning plan with ID: " + planId);
      LearningPlan learningPlan = learningPlanRepository.findById(planId)
          .orElseThrow(() -> {
            System.err.println("Learning plan not found with id: " + planId);
            return new ResourceNotFoundException("Learning plan not found with id: " + planId);
          });
      
      System.out.println("Found learning plan: " + learningPlan.getTitle());
      LearningPlanResponse response = mapToResponse(learningPlan);
      System.out.println("Mapped learning plan to response DTO");
      return response;
    } catch (ResourceNotFoundException e) {
      System.err.println("Resource not found: " + e.getMessage());
      throw e;
    } catch (Exception e) {
      System.err.println("Error in getByPlanId: " + e.getMessage());
      e.printStackTrace();
      throw e;
    }
  }

  @Override
  public LearningPlan updateById(String userId, LearningPlanRQ rq, String learningPlanId) {
    LearningPlan learningPlan = learningPlanRepository.findById(learningPlanId)
        .orElseThrow(
            () -> new ResourceNotFoundException(
                "Not found learning plan with id: " + learningPlanId));

    // Save the original userId to ensure it's not overwritten
    String originalUserId = learningPlan.getUserId();
    Integer originalFollowers = learningPlan.getFollowers();
    Boolean originalFollowing = learningPlan.getFollowing();
    
    // Update the learning plan with the incoming data
    BeanUtils.copyProperties(rq, learningPlan);
    
    // Ensure important fields are preserved
    learningPlan.setUserId(originalUserId);
    if (rq.getFollowers() == null) {
      learningPlan.setFollowers(originalFollowers);
    }
    if (rq.getFollowing() == null) {
      learningPlan.setFollowing(originalFollowing);
    }
    
    // Process topics to ensure correct status conversion
    if (rq.getTopics() != null) {
      for (Topic topic : learningPlan.getTopics()) {
        // Make sure the completed/status values are properly synchronized
        // This ensures the boolean 'completed' field from frontend maps to TopicStatus enum
        boolean isCompleted = topic.isCompleted();
        topic.setCompleted(isCompleted);
      }
    }
    
    return learningPlanRepository.save(learningPlan);
  }

  @Override
  public void delete(String userId, String learningPlanId) {
    LearningPlan learningPlan = learningPlanRepository.findById(learningPlanId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Not found learning plan with id: " + learningPlanId));

    learningPlanRepository.delete(learningPlan);
  }
  
  @Override
  public LearningPlanResponse followPlan(String planId, String userId) {
    LearningPlan learningPlan = learningPlanRepository.findById(planId)
        .orElseThrow(() -> new ResourceNotFoundException("Learning plan not found with id: " + planId));
    
    learningPlan.setFollowers(learningPlan.getFollowers() != null ? learningPlan.getFollowers() + 1 : 1);
    learningPlan.setFollowing(true);
    
    learningPlanRepository.save(learningPlan);
    
    return mapToResponse(learningPlan);
  }
  
  @Override
  public LearningPlanResponse unfollowPlan(String planId, String userId) {
    LearningPlan learningPlan = learningPlanRepository.findById(planId)
        .orElseThrow(() -> new ResourceNotFoundException("Learning plan not found with id: " + planId));
    
    if (learningPlan.getFollowers() != null && learningPlan.getFollowers() > 0) {
      learningPlan.setFollowers(learningPlan.getFollowers() - 1);
    }
    learningPlan.setFollowing(false);
    
    learningPlanRepository.save(learningPlan);
    
    return mapToResponse(learningPlan);
  }
  
  private LearningPlanResponse mapToResponse(LearningPlan learningPlan) {
    try {
      // Build response with available data even if user is not found
      LearningPlanResponse response = LearningPlanResponse.builder()
          .id(learningPlan.getId())
          .title(learningPlan.getTitle())
          .description(learningPlan.getDescription())
          .subject(learningPlan.getSubject())
          .createdAt(learningPlan.getCreatedAt())
          .topics(learningPlan.getTopics())
          .resources(learningPlan.getResources())
          .estimatedDays(learningPlan.getEstimatedDays())
          .followers(learningPlan.getFollowers())
          .userId(learningPlan.getUserId())
          .following(learningPlan.getFollowing())
          .completionPercentage(learningPlan.getCompletionPercentage())
          .build(); // Build the initial response
    
      // Try to get user information, but continue even if not found
      try {
        if (learningPlan.getUserId() != null) {
          User user = userRepository.findById(learningPlan.getUserId())
              .orElse(null);
          
          if (user != null) {
            UserProfile userProfile = user.getUserProfile();
            
            UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .name(userProfile != null ? userProfile.getFullName() : user.getEmail())
                .username(user.getEmail())
                .profilePicture(userProfile != null ? userProfile.getProfilePictureUrl() : null)
                .build();
                
            // Create a new response with the user information
            response = LearningPlanResponse.builder()
                .id(response.getId())
                .title(response.getTitle())
                .description(response.getDescription())
                .subject(response.getSubject())
                .createdAt(response.getCreatedAt())
                .topics(response.getTopics())
                .resources(response.getResources())
                .estimatedDays(response.getEstimatedDays())
                .followers(response.getFollowers())
                .userId(response.getUserId())
                .following(response.getFollowing())
                .completionPercentage(response.getCompletionPercentage())
                .user(userDTO)
                .build();
          } else {
            // Create a placeholder user if not found
            UserDTO placeholderUser = UserDTO.builder()
                .id(learningPlan.getUserId())
                .name("Unknown User")
                .username("unknown@example.com")
                .build();
                
            // Create a new response with the placeholder user
            response = LearningPlanResponse.builder()
                .id(response.getId())
                .title(response.getTitle())
                .description(response.getDescription())
                .subject(response.getSubject())
                .createdAt(response.getCreatedAt())
                .topics(response.getTopics())
                .resources(response.getResources())
                .estimatedDays(response.getEstimatedDays())
                .followers(response.getFollowers())
                .userId(response.getUserId())
                .following(response.getFollowing())
                .completionPercentage(response.getCompletionPercentage())
                .user(placeholderUser)
                .build();
            System.out.println("User not found for learning plan ID: " + learningPlan.getId() + ", using placeholder");
          }
        }
      } catch (Exception e) {
        System.err.println("Error fetching user for learning plan: " + e.getMessage());
        // Create a placeholder user on error
        UserDTO placeholderUser = UserDTO.builder()
            .id(learningPlan.getUserId())
            .name("Unknown User")
            .username("unknown@example.com")
            .build();
            
        // Create a new response with the placeholder user
        response = LearningPlanResponse.builder()
            .id(response.getId())
            .title(response.getTitle())
            .description(response.getDescription())
            .subject(response.getSubject())
            .createdAt(response.getCreatedAt())
            .topics(response.getTopics())
            .resources(response.getResources())
            .estimatedDays(response.getEstimatedDays())
            .followers(response.getFollowers())
            .userId(response.getUserId())
            .following(response.getFollowing())
            .completionPercentage(response.getCompletionPercentage())
            .user(placeholderUser)
            .build();
      }
      
      return response;
    } catch (Exception e) {
      System.err.println("Error mapping learning plan to response: " + e.getMessage());
      throw e;
    }
  }
}

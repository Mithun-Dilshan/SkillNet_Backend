package com.skillshare.skill_platform.controller;

import com.skillshare.skill_platform.dto.LearningPlanRQ;
import com.skillshare.skill_platform.dto.LearningPlanResponse;
import com.skillshare.skill_platform.entity.LearningPlan;
import com.skillshare.skill_platform.exception.ResourceNotFoundException;
import com.skillshare.skill_platform.service.LearningPlanService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class LearningPlanController {

  private LearningPlanService learningPlanService;

  @PostMapping("/learning-plans")
  public LearningPlan create(@RequestBody LearningPlanRQ rq,
      @RequestParam(value = "userId", required = false, defaultValue = "1") String userId) {
    return learningPlanService.create(rq, userId);
  }

  @GetMapping("/learning-plans")
  public ResponseEntity<List<LearningPlanResponse>> getAllLearningPlans() {
    try {
      System.out.println("Fetching all learning plans");
      List<LearningPlanResponse> result = learningPlanService.getAll();
      System.out.println("Retrieved " + result.size() + " learning plans");
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      System.err.println("Error getting all learning plans: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.internalServerError().build();
    }
  }
  
  @GetMapping("/learning-plans/user")
  public ResponseEntity<List<LearningPlanResponse>> getLearningPlansByUserId(
      @RequestParam(value = "userId", required = true) String userId) {
    try {
      if (userId == null || userId.isEmpty()) {
        return ResponseEntity.badRequest().build();
      }
      System.out.println("Fetching learning plans for user: " + userId);
      List<LearningPlanResponse> result = learningPlanService.getById(userId);
      System.out.println("Retrieved " + result.size() + " learning plans for user: " + userId);
      return ResponseEntity.ok(result);
    } catch (ResourceNotFoundException e) {
      System.err.println("Resource not found: " + e.getMessage());
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      System.err.println("Error getting learning plans by user ID: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.internalServerError().build();
    }
  }

  @GetMapping("/learning-plans/{learning-plan-id}")
  public ResponseEntity<LearningPlanResponse> getLearningPlanById(
      @PathVariable("learning-plan-id") String learningPlanId) {
    try {
      System.out.println("Fetching learning plan with ID: " + learningPlanId);
      LearningPlanResponse response = learningPlanService.getByPlanId(learningPlanId);
      System.out.println("Successfully retrieved learning plan: " + response.getTitle());
      return ResponseEntity.ok(response);
    } catch (ResourceNotFoundException e) {
      System.err.println("Learning plan not found: " + e.getMessage());
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      System.err.println("Error retrieving learning plan: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.internalServerError().build();
    }
  }

  @PutMapping("/learning-plans/{learning-plan-id}")
  public LearningPlan update(
      @PathVariable("learning-plan-id") String learningPlanId,
      @RequestBody LearningPlanRQ rq) throws ResourceNotFoundException {
    // Using a default user ID since we're removing the check
    return learningPlanService.updateById("1", rq, learningPlanId);
  }

  @DeleteMapping("/learning-plans/{learning-plan-id}")
  public void deleteById(
      @PathVariable("learning-plan-id") String learningPlanId) throws ResourceNotFoundException {
    learningPlanService.delete("1", learningPlanId);
  }
  
  @PostMapping("/learning-plans/{learning-plan-id}/follow")
  public ResponseEntity<LearningPlanResponse> followPlan(
      @PathVariable("learning-plan-id") String learningPlanId,
      @RequestBody String userId) {
    return ResponseEntity.ok(learningPlanService.followPlan(learningPlanId, userId));
  }
  
  @PostMapping("/learning-plans/{learning-plan-id}/unfollow")
  public ResponseEntity<LearningPlanResponse> unfollowPlan(
      @PathVariable("learning-plan-id") String learningPlanId,
      @RequestBody String userId) {
    return ResponseEntity.ok(learningPlanService.unfollowPlan(learningPlanId, userId));
  }
}

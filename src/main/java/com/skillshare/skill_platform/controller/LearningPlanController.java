package com.skillshare.skill_platform.controller;

import com.skillshare.skill_platform.dto.LearningPlanRQ;
import com.skillshare.skill_platform.dto.LearningPlanResponse;
import com.skillshare.skill_platform.entity.LearningPlan;
import com.skillshare.skill_platform.exception.ResourceNotFoundException;
import com.skillshare.skill_platform.service.LearningPlanService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class LearningPlanController {

  private LearningPlanService learningPlanService;

  @PostMapping("/users/{user-id}/learning-plans")
  public LearningPlan create(@RequestBody LearningPlanRQ rq,
      @PathVariable("user-id") String userId) {
    return learningPlanService.create(rq, userId);
  }

  @GetMapping("/learning-plans")
  public List<LearningPlanResponse> list() {
    return learningPlanService.getAll();
  }

  @GetMapping("/users/{user-id}/learning-plans")
  public List<LearningPlanResponse> getById(@PathVariable("user-id") String userId)
      throws ResourceNotFoundException {
    return learningPlanService.getById(userId);
  }

  @PutMapping("/users/{user-id}/learning-plans/{learning-plan-id}")
  public LearningPlan update(@PathVariable("user-id") String userId,
      @PathVariable("learning-plan-id") String learningPlanId,
      @RequestBody LearningPlanRQ rq) throws ResourceNotFoundException {
    return learningPlanService.updateById(userId, rq, learningPlanId);
  }

  @DeleteMapping("/users/{user-id}/learning-plans/{learning-plan-id}")
  public void deleteById(@PathVariable("user-id") String userId,
      @PathVariable("learning-plan-id") String learningPlanId) throws ResourceNotFoundException {
    learningPlanService.delete(userId, learningPlanId);
  }
}

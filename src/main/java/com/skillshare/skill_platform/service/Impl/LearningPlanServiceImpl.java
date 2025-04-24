package com.skillshare.skill_platform.service.Impl;

import com.skillshare.skill_platform.dto.LearningPlanRQ;
import com.skillshare.skill_platform.dto.LearningPlanResponse;
import com.skillshare.skill_platform.entity.LearningPlan;
import com.skillshare.skill_platform.entity.Topic;
import com.skillshare.skill_platform.exception.ResourceNotFoundException;
import com.skillshare.skill_platform.repository.LearningPlanRepository;
import com.skillshare.skill_platform.service.LearningPlanService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LearningPlanServiceImpl implements LearningPlanService {

  private final LearningPlanRepository learningPlanRepository;

  @Override
  public LearningPlan create(LearningPlanRQ rq, String userId) {
    LearningPlan learningPlan = new LearningPlan();
    BeanUtils.copyProperties(rq, learningPlan);
    learningPlan.setCreatedAt(LocalDateTime.now());
    learningPlan.setUserId(rq.getUserId());
    return learningPlanRepository.save(learningPlan);
  }

  @Override
  public List<LearningPlanResponse> getAll() {
    List<LearningPlan> learningPlans = learningPlanRepository.findAll();

    return learningPlans.stream()
        .map(learningPlan -> LearningPlanResponse.builder()
            .id(learningPlan.getId())
            .learningPlanName(learningPlan.getLearningPlanName())
            .learningPlanDescription(learningPlan.getLearningPlanDescription())
            .stream(learningPlan.getStream())
            .createdAt(learningPlan.getCreatedAt())
            .userId(learningPlan.getUserId())
            .topicIds(
                learningPlan.getTopics() != null
                    ? learningPlan.getTopics().stream()
                    .map(Topic::getId)
                    .toList()
                    : List.of()
            )
            .build())
        .toList();
  }

  @Override
  public List<LearningPlanResponse> getById(String userId) {
    List<LearningPlan> learningPlans = learningPlanRepository.findByUserId(userId);

    if (learningPlans.isEmpty()) {
      throw new ResourceNotFoundException("Not found learning plans for user with id: " + userId);
    }

    return learningPlans.stream()
        .map(learningPlan -> LearningPlanResponse.builder()
            .id(learningPlan.getId())
            .learningPlanName(learningPlan.getLearningPlanName())
            .learningPlanDescription(learningPlan.getLearningPlanDescription())
            .stream(learningPlan.getStream())
            .createdAt(learningPlan.getCreatedAt())
            .userId(learningPlan.getUserId())
            .topicIds(
                learningPlan.getTopics() != null
                    ? learningPlan.getTopics().stream()
                    .map(Topic::getId)
                    .toList()
                    : List.of()
            )
            .build())
        .toList();
  }

  @Override
  public LearningPlan updateById(String userId, LearningPlanRQ rq, String learningPlanId) {
    LearningPlan learningPlan = learningPlanRepository.findByUserIdAndId(userId, learningPlanId)
        .orElseThrow(
            () -> new ResourceNotFoundException(
                "Not found learning plan with id: " + learningPlanId));

    BeanUtils.copyProperties(rq, learningPlan);
    learningPlan.setCreatedAt(LocalDateTime.now());
    learningPlan.setUserId(rq.getUserId());
    return learningPlanRepository.save(learningPlan);
  }

  @Override
  public void delete(String userId, String learningPlanId) {
    LearningPlan learningPlan = learningPlanRepository.findByUserIdAndId(userId, learningPlanId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Not found learning plan with id: " + learningPlanId));

    learningPlanRepository.delete(learningPlan);
  }
}

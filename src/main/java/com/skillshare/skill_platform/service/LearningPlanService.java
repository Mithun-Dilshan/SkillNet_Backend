package com.skillshare.skill_platform.service;

import com.skillshare.skill_platform.dto.LearningPlanRQ;
import com.skillshare.skill_platform.dto.LearningPlanResponse;
import com.skillshare.skill_platform.entity.LearningPlan;
import java.util.List;

public interface LearningPlanService {

  LearningPlan create(LearningPlanRQ rq, String userId);

  List<LearningPlanResponse> getAll();

  List<LearningPlanResponse> getById(String userId);
  
  LearningPlanResponse getByPlanId(String planId);

  LearningPlan updateById(String userId, LearningPlanRQ rq, String learningPlanId);

  void delete(String userId, String learningPlanId);
  
  LearningPlanResponse followPlan(String planId, String userId);
  
  LearningPlanResponse unfollowPlan(String planId, String userId);
}

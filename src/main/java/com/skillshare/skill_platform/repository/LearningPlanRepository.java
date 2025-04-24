package com.skillshare.skill_platform.repository;

import com.skillshare.skill_platform.entity.LearningPlan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LearningPlanRepository extends MongoRepository<LearningPlan, String> {

  List<LearningPlan> findByUserId(String userId);

  Optional<LearningPlan> findByUserIdAndId(String userId, String learningPlanId);
}

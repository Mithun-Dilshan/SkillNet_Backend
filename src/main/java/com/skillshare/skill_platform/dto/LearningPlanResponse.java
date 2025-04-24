package com.skillshare.skill_platform.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LearningPlanResponse {

  private String id;
  private String learningPlanName;
  private String learningPlanDescription;
  private String stream;
  private LocalDateTime createdAt;
  private List<TopicRq> topics;
  private String userId;
}

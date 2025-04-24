package com.skillshare.skill_platform.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class LearningPlanRQ {

  private String id;
  private String learningPlanName;
  private String learningPlanDescription;
  private String stream;
  private LocalDateTime createdAt;
  private List<String> topicIds = new ArrayList<>();
  private String userId;
}

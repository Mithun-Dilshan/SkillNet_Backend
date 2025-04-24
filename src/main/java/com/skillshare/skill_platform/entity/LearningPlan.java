package com.skillshare.skill_platform.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
public class LearningPlan {

  @Id
  private String id;
  private String learningPlanName;
  private String learningPlanDescription;
  private String stream;
  private LocalDateTime createdAt;
  private List<Topic> topics = new ArrayList<>();
  private String userId;
}

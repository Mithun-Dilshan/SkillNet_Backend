package com.skillshare.skill_platform.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "learning_plans")
@Data
public class LearningPlan {

  @Id
  private String id;
  private String title;
  private String description;
  private String subject;
  private LocalDateTime createdAt;
  private List<Topic> topics = new ArrayList<>();
  private List<Resource> resources = new ArrayList<>();
  private Integer estimatedDays;
  private Integer followers;
  private String userId;
  private Boolean following;
  
  public int getCompletionPercentage() {
    if (topics == null || topics.isEmpty()) {
      return 0;
    }
    
    long completedCount = topics.stream()
        .filter(topic -> topic.getStatus() == TopicStatus.COMPLETED)
        .count();
        
    return (int) ((completedCount * 100) / topics.size());
  }
}

package com.skillshare.skill_platform.dto;

import com.skillshare.skill_platform.entity.Resource;
import com.skillshare.skill_platform.entity.Topic;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPlanResponse {

  private String id;
  private String title;
  private String description;
  private String subject;
  private LocalDateTime createdAt;
  private List<Topic> topics;
  private List<Resource> resources;
  private Integer estimatedDays;
  private Integer followers;
  private String userId;
  private Boolean following;
  private Integer completionPercentage;
  private UserDTO user;
}

package com.skillshare.skill_platform.dto;

import com.skillshare.skill_platform.entity.Resource;
import com.skillshare.skill_platform.entity.Topic;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class LearningPlanRQ {

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
}

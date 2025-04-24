package com.skillshare.skill_platform.dto;

import com.skillshare.skill_platform.entity.TopicStatus;
import lombok.Data;

@Data
public class TopicRq {

  private String name;
  private TopicStatus status;
}

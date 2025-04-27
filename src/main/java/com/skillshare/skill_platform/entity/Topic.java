package com.skillshare.skill_platform.entity;

import lombok.Data;

@Data
public class Topic {

  private String id;
  private String title;
  private TopicStatus status;
  
  public boolean isCompleted() {
    return status == TopicStatus.COMPLETED;
  }
  
  public void setCompleted(boolean completed) {
    this.status = completed ? TopicStatus.COMPLETED : TopicStatus.IN_PROGRESS;
  }
}

package com.skillshare.skill_platform.entity;

import lombok.Data;

@Data
public class Topic {

  private String id;
  private String title;
  private TopicStatus status;
  
  // Helper method to match frontend's "completed" boolean
  public boolean isCompleted() {
    return status == TopicStatus.COMPLETED;
  }
  
  // Helper method to set status from completed flag
  public void setCompleted(boolean completed) {
    this.status = completed ? TopicStatus.COMPLETED : TopicStatus.IN_PROGRESS;
  }
}

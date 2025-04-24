package com.skillshare.skill_platform.entity;

import lombok.Data;

@Data
public class Topic {

  private String id;
  private String name;
  private TopicStatus status;
}

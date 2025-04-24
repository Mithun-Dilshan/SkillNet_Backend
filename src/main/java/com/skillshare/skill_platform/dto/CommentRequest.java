package com.skillshare.skill_platform.dto;

import lombok.Data;

@Data
public class CommentRequest {
    private String userId;
    private String content;
} 
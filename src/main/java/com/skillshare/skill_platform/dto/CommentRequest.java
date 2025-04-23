package com.skillshare.skill_platform.dto;

import lombok.Data;

@Data
public class CommentRequest {
    private String uid;
    private String uname;
    private String text;
} 
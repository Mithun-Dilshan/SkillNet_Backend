package com.skillshare.skill_platform.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostRequest {
    private String userId;
    private String description;
    private MultipartFile file;
} 
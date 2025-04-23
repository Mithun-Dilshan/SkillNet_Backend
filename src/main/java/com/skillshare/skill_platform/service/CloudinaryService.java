package com.skillshare.skill_platform.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    public String uploadFile(MultipartFile file, String folderName);
} 
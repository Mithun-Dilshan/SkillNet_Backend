package com.skillshare.skill_platform.service.Impl;

import com.skillshare.skill_platform.service.CloudinaryService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Primary
@ConditionalOnProperty(name = "cloudinary.enabled", havingValue = "false")
public class MockCloudinaryServiceImpl implements CloudinaryService {

    @Override
    public String uploadFile(MultipartFile file, String folderName) {
        // Generate a random file name for development
        String fileName = UUID.randomUUID().toString();
        String extension = getFileExtension(file.getOriginalFilename());
        return "https://mock-cloudinary-url.com/" + folderName + "/" + fileName + "." + extension;
    }
    
    private String getFileExtension(String filename) {
        if (filename == null) return "jpg";
        int lastDotPosition = filename.lastIndexOf('.');
        if (lastDotPosition > 0) {
            return filename.substring(lastDotPosition + 1);
        }
        return "jpg";
    }
} 
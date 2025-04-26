package com.skillshare.skill_platform.service.Impl;

import com.cloudinary.Cloudinary;
import com.skillshare.skill_platform.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "cloudinary.enabled", havingValue = "true", matchIfMissing = true)
public class CloudinaryServiceImpl implements CloudinaryService {

    @Autowired(required = false)
    private Cloudinary cloudinary;
    
    @Value("${cloudinary.enabled:false}")
    private boolean cloudinaryEnabled;

    @Override
    public String uploadFile(MultipartFile file, String folderName) {
        // If Cloudinary is disabled, return a mock URL
        if (!cloudinaryEnabled || cloudinary == null) {
            // Generate a random file name for development
            String fileName = UUID.randomUUID().toString();
            String extension = getFileExtension(file.getOriginalFilename());
            return "https://mock-cloudinary-url.com/" + folderName + "/" + fileName + "." + extension;
        }
        
        try {
            HashMap<Object, Object> options = new HashMap<>();
            options.put("folder", folderName);
            options.put("resource_type", "auto"); 

            Map<String, Object> uploadedFile = cloudinary.uploader().upload(file.getBytes(), options);
            String publicId = (String) uploadedFile.get("public_id");
            String format = (String) uploadedFile.get("format");  

            // Check if the uploaded file is a video
            if ("video".equals(uploadedFile.get("resource_type"))) {
                // Return video URL
                return cloudinary.url().resourceType("video").format(format).secure(true).generate(publicId);
            } else {
                // Return image URL
                return cloudinary.url().secure(true).generate(publicId);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
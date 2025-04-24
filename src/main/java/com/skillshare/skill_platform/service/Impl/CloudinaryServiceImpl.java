package com.skillshare.skill_platform.service.Impl;

import com.cloudinary.Cloudinary;
import com.skillshare.skill_platform.service.CloudinaryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    @Resource
    private Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file, String folderName) {
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
} 
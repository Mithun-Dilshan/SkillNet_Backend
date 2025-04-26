package com.skillshare.skill_platform.service.Impl;

import com.cloudinary.Cloudinary;
import com.skillshare.skill_platform.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file, String folderName) {
        if (file == null || file.isEmpty()) {
            System.out.println("File is null or empty, cannot upload to Cloudinary");
            return null;
        }
        
        try {
            System.out.println("Attempting to upload file to Cloudinary, size: " + file.getSize() + " bytes");
            
            HashMap<String, Object> options = new HashMap<>();
            options.put("folder", folderName);
            options.put("resource_type", "auto"); 
            options.put("unique_filename", true);

            Map<String, Object> uploadedFile = cloudinary.uploader().upload(file.getBytes(), options);
            
            System.out.println("Successfully uploaded to Cloudinary, response: " + uploadedFile);
            
            String publicId = (String) uploadedFile.get("public_id");
            String format = (String) uploadedFile.get("format");
            String secureUrl = (String) uploadedFile.get("secure_url");

            // If secure_url is present, use it directly
            if (secureUrl != null && !secureUrl.isEmpty()) {
                return secureUrl;
            }

            // Otherwise, construct the URL
            if ("video".equals(uploadedFile.get("resource_type"))) {
                // Return video URL
                return cloudinary.url().resourceType("video").format(format).secure(true).generate(publicId);
            } else {
                // Return image URL
                return cloudinary.url().secure(true).format(format).generate(publicId);
            }

        } catch (IOException e) {
            System.err.println("Error uploading file to Cloudinary: " + e.getMessage());
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
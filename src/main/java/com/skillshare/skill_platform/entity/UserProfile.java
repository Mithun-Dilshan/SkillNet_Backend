package com.skillshare.skill_platform.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_profiles")
@Data
public class UserProfile {
    @Id
    private String id;
    private String userId;
    private String bio;
    private String profilePictureUrl;
    private String fullName;
    

}
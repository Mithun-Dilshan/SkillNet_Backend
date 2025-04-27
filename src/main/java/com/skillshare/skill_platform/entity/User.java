package com.skillshare.skill_platform.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Document(collection = "users")
@Data
public class User {
    @Id
    private String id;
    private String email;
    private String name;
    private String password;
    private String oauthProvider;
    private String oauthId;
    
    @DBRef
    private UserProfile userProfile;

}
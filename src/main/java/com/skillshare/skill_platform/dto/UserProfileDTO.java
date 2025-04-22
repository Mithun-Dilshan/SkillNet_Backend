package com.skillshare.skill_platform.dto;


import lombok.Data;

@Data
public class UserProfileDTO {
    private String id;
    private String userId;
    private String bio;
    private String profilePictureUrl;
}
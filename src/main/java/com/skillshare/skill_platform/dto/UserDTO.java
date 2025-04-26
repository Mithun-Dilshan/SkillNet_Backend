package com.skillshare.skill_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String name;
    private String username;
    private String profilePicture;
    private String email;
    
    // Add backwards compatibility for old code
    public void setEmail(String email) {
        this.email = email;
    }
}
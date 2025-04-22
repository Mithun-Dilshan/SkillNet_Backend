package com.skillshare.skill_platform.repository;



import org.springframework.data.mongodb.repository.MongoRepository;

import com.skillshare.skill_platform.entity.UserProfile;

public interface UserProfileRepository extends MongoRepository<UserProfile, String> {
    UserProfile findByUserId(String userId);
}

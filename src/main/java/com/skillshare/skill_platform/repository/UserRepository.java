package com.skillshare.skill_platform.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.skillshare.skill_platform.entity.User;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name);
    Optional<User> findByOauthProviderAndOauthId(String oauthProvider, String oauthId);
    Optional<User> findByOauthId(String oauthId);
    
    // Custom query to find users by partial name match (case-insensitive)
    @Query("{ 'name' : { $regex: ?0, $options: 'i' } }")
    List<User> findByNameContainingIgnoreCase(String namePartial);
}
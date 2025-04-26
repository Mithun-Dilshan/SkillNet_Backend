package com.skillshare.skill_platform.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.skillshare.skill_platform.entity.User;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByOauthProviderAndOauthId(String oauthProvider, String oauthId);
}
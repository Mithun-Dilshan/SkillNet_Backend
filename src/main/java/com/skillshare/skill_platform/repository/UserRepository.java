package com.skillshare.skill_platform.repository;



import org.springframework.data.mongodb.repository.MongoRepository;

import com.skillshare.skill_platform.entity.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}
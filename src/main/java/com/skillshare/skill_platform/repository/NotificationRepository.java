// package com.skillshare.skill_platform.repository;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.mongodb.repository.MongoRepository;
// import org.springframework.stereotype.Repository;

// import com.skillshare.skill_platform.entity.Notification;

// @Repository
// public interface NotificationRepository extends MongoRepository<Notification, String> {
//     Page<Notification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
//     long countByUserIdAndReadFalse(String userId);
// }

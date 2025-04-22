// package com.skillshare.skill_platform.repository;

// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.mongodb.repository.MongoRepository;
// import org.springframework.stereotype.Repository;

// import com.skillshare.skill_platform.entity.Comment;

// @Repository
// public interface CommentRepository extends MongoRepository<Comment, String> {
//     Page<Comment> findByPostId(String postId, Pageable pageable);
//     long countByPostId(String postId);
//     void deleteByPostId(String postId);
// }

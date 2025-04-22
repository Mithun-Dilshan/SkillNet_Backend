// package com.skillshare.skill_platform.repository;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.mongodb.repository.MongoRepository;
// import org.springframework.stereotype.Repository;

// import com.skillshare.skill_platform.entity.Post;
// import com.skillshare.skill_platform.entity.PostType;

// import java.util.List;

// @Repository
// public interface PostRepository extends MongoRepository<Post, String> {
//     Page<Post> findByUserId(String userId, Pageable pageable);
//     Page<Post> findByType(PostType type, Pageable pageable);
//     Page<Post> findByUserIdIn(List<String> userIds, Pageable pageable);
// }

package com.skillshare.skill_platform.service;

import com.skillshare.skill_platform.dto.CommentRequest;
import com.skillshare.skill_platform.entity.Comment;

import java.util.List;

public interface CommentService {
    Comment createComment(String postId, CommentRequest commentRequest);
    List<Comment> getAllCommentsByPostId(String postId);
    boolean deleteComment(String commentId, String userId);
    boolean updateComment(String commentId, String userId, CommentRequest commentRequest);
} 
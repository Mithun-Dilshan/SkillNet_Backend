package com.skillshare.skill_platform.service.Impl;

import com.skillshare.skill_platform.dto.CommentRequest;
import com.skillshare.skill_platform.entity.Comment;
import com.skillshare.skill_platform.repository.CommentRepository;
import com.skillshare.skill_platform.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Comment createComment(String postId, CommentRequest commentRequest) {
        Comment newComment = new Comment();
        newComment.setPostId(postId);
        newComment.setContent(commentRequest.getContent());
        newComment.setUserId(commentRequest.getUserId());
        newComment.setCreatedAt(new Date());
        return commentRepository.save(newComment);
    }

    @Override
    public List<Comment> getAllCommentsByPostId(String postId) {
        List<Comment> allComments = commentRepository.findAll();
        List<Comment> commentsForPost = new ArrayList<>();

        for (Comment comment : allComments) {
            if (comment.getPostId().equals(postId)) {
                commentsForPost.add(comment);
            }
        }

        return commentsForPost;
    }

    @Override
    public boolean updateComment(String commentId, String userId, CommentRequest commentRequest) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
           
            if (comment.getUserId().equals(userId)) {
                comment.setContent(commentRequest.getContent());
                commentRepository.save(comment);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteComment(String commentId, String userId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
           
            if (comment.getUserId().equals(userId)) {
                commentRepository.delete(comment);
                return true; 
            }
        }
        return false;
    }
} 
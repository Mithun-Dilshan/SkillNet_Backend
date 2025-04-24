package com.skillshare.skill_platform.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "post")
@Data
public class Post {
    @Id
    private String id;

    private String userId;

    private String description;

    private String url;

    private List<Comment> comments = new ArrayList<>();
    private List<Like> likes = new ArrayList<>();

    @CreatedDate
    private Date date;
} 
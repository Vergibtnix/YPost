package com.example.YPost.web.dto;

import java.time.LocalDateTime;

public record PostView(
        Long id,
        String content,
        LocalDateTime createdAt,
        String authorUsername,
        long likeCount,
        long dislikeCount,
        boolean likedByCurrentUser,
        boolean dislikedByCurrentUser,
        boolean ownPost
) {}

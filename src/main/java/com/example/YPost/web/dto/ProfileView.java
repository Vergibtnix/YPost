package com.example.YPost.web.dto;

import java.util.List;

public record ProfileView(
        String username,
        String email,
        String bio,
        long postCount,
        long totalLikes,
        boolean ownProfile,
        List<PostView> posts
) {
}


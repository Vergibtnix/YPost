package com.example.YPost.web.controller;

import com.example.YPost.model.User;
import com.example.YPost.service.CurrentUserService;
import com.example.YPost.service.PostService;
import com.example.YPost.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final PostService postService;
    private final CurrentUserService currentUserService;

    @GetMapping("/{username}")
    public String profile(@PathVariable String username,
                          Authentication authentication,
                          Model model) {
        User currentUser = currentUserService.requireCurrentUser(authentication);
        User profileOwner = userService.getByUsername(username);
        boolean ownProfile = currentUser.getId().equals(profileOwner.getId());

        model.addAttribute("pageTitle", "Profil von " + profileOwner.getUsername());
        model.addAttribute(
                "profile",
                userService.buildProfileView(
                        profileOwner,
                        ownProfile,
                        postService.getPostsForProfile(profileOwner, currentUser)
                )
        );
        model.addAttribute("currentUsername", currentUser.getUsername());
        return "profile";
    }
}


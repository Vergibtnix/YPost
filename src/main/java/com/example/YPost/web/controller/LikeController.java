package com.example.YPost.web.controller;

import com.example.YPost.model.User;
import com.example.YPost.service.CurrentUserService;
import com.example.YPost.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class LikeController {

    private final PostService postService;
    private final CurrentUserService currentUserService;

    @PostMapping("/{postId}/like")
    public String toggleLike(@PathVariable Long postId,
                             @RequestParam(name = "redirect", defaultValue = "/feed") String redirect,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        User currentUser = currentUserService.requireCurrentUser(authentication);
        try {
            postService.toggleLike(postId, currentUser);
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }

        boolean safePath = redirect != null && redirect.startsWith("/") && !redirect.startsWith("//");
        String safeRedirect = safePath ? redirect : "/feed";
        return "redirect:" + safeRedirect;
    }
}


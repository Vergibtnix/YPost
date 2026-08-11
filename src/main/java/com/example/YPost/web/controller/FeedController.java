package com.example.YPost.web.controller;

import com.example.YPost.model.User;
import com.example.YPost.service.CurrentUserService;
import com.example.YPost.service.PostService;
import com.example.YPost.web.form.PostForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final PostService postService;
    private final CurrentUserService currentUserService;

    @ModelAttribute("postForm")
    public PostForm postForm() {
        return new PostForm();
    }

    @GetMapping
    public String feed(@RequestParam(name = "sort", required = false) String sort,
                       Authentication authentication,
                       Model model) {
        User currentUser = currentUserService.requireCurrentUser(authentication);
        String normalizedSort = postService.normalizeSort(sort);

        model.addAttribute("pageTitle", "Feed");
        model.addAttribute("sort", normalizedSort);
        model.addAttribute("posts", postService.getFeed(normalizedSort, currentUser));
        model.addAttribute("currentUsername", currentUser.getUsername());
        return "feed";
    }

    @PostMapping
    public String createPost(@RequestParam(name = "sort", required = false) String sort,
                             @Valid @ModelAttribute("postForm") PostForm form,
                             BindingResult bindingResult,
                             Authentication authentication,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        User currentUser = currentUserService.requireCurrentUser(authentication);
        String normalizedSort = postService.normalizeSort(sort);

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Feed");
            model.addAttribute("sort", normalizedSort);
            model.addAttribute("posts", postService.getFeed(normalizedSort, currentUser));
            model.addAttribute("currentUsername", currentUser.getUsername());
            return "feed";
        }

        try {
            postService.createPost(currentUser, form);
            redirectAttributes.addFlashAttribute("successMessage", "Post wurde erstellt.");
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("post.error", exception.getMessage());
            model.addAttribute("pageTitle", "Feed");
            model.addAttribute("sort", normalizedSort);
            model.addAttribute("posts", postService.getFeed(normalizedSort, currentUser));
            model.addAttribute("currentUsername", currentUser.getUsername());
            return "feed";
        }

        return "redirect:/feed?sort=" + normalizedSort;
    }
}


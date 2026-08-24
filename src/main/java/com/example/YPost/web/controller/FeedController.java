package com.example.YPost.web.controller;

import com.example.YPost.model.User;
import com.example.YPost.service.CurrentUserService;
import com.example.YPost.service.PostService;
import com.example.YPost.service.UserService;
import com.example.YPost.web.dto.PostView;
import com.example.YPost.web.form.PostForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;

@Controller
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final PostService postService;
    private final CurrentUserService currentUserService;
    private final UserService userService;

    @ModelAttribute("postForm")
    public PostForm postForm() {
        return new PostForm();
    }

    // ⭐ NEU: Feed mit Pagination
    @GetMapping
    public String feed(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(name = "sort", required = false) String sort,
                       Authentication authentication,
                       Model model) {

        User currentUser = currentUserService.requireCurrentUser(authentication);
        String normalizedSort = postService.normalizeSort(sort);

        Page<PostView> feedPage = postService.getFeedPaged(page, size, currentUser);

        model.addAttribute("pageTitle", "Feed");
        model.addAttribute("sort", normalizedSort);
        model.addAttribute("posts", feedPage.getContent());
        model.addAttribute("currentUsername", currentUser.getUsername());

        // Pagination Infos
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", feedPage.getTotalPages());

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

    @PostMapping("/{postId}/delete")
    public String deletePost(@PathVariable Long postId,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes,
                             @RequestParam(name = "sort", required = false) String sort) {

        User currentUser = currentUserService.requireCurrentUser(authentication);
        String normalizedSort = postService.normalizeSort(sort);

        try {
            postService.deletePost(postId, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Post wurde gelöscht.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }

        return "redirect:/feed?sort=" + normalizedSort;
    }

    @GetMapping("/search")
    public String searchUsers(@RequestParam("q") String query,
                              Authentication authentication,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        User currentUser = currentUserService.requireCurrentUser(authentication);

        List<User> results = userService.searchUsers(query);

        // ⭐ 1 Treffer → direkt weiterleiten zum Profil
        if (results.size() == 1) {
            String username = results.get(0).getUsername();
            return "redirect:/users/" + username;
        }

        // ⭐ 0 Treffer → Meldung
        if (results.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Kein Benutzer gefunden.");
            return "redirect:/feed";
        }

        // ⭐ Mehrere Treffer → Liste anzeigen
        model.addAttribute("pageTitle", "User-Suche");
        model.addAttribute("query", query);
        model.addAttribute("results", results);
        model.addAttribute("currentUsername", currentUser.getUsername());

        return "search-users";
    }

}

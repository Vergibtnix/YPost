package com.example.YPost.web.controller;

import com.example.YPost.service.CurrentUserService;
import com.example.YPost.service.UserService;
import com.example.YPost.web.form.RegistrationForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final CurrentUserService currentUserService;

    @ModelAttribute("registrationForm")
    public RegistrationForm registrationForm() {
        return new RegistrationForm();
    }

    @GetMapping("/")
    public String root(Authentication authentication) {
        return currentUserService.getCurrentUser(authentication).isPresent()
                ? "redirect:/feed"
                : "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        return currentUserService.getCurrentUser(authentication).isPresent()
                ? "redirect:/feed"
                : "login";
    }

    @GetMapping("/register")
    public String registerPage(Authentication authentication) {
        return currentUserService.getCurrentUser(authentication).isPresent()
                ? "redirect:/feed"
                : "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registrationForm") RegistrationForm form,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Registrieren");
            return "register";
        }

        try {
            userService.register(form);
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("register.error", exception.getMessage());
            model.addAttribute("pageTitle", "Registrieren");
            return "register";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Registrierung erfolgreich. Bitte jetzt einloggen.");
        return "redirect:/login";
    }
}


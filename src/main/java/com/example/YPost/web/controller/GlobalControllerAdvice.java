package com.example.YPost.web.controller;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class, UsernameNotFoundException.class})
    public String handleKnownExceptions(Exception exception, Model model) {
        model.addAttribute("pageTitle", "Fehler");
        model.addAttribute("errorMessage", exception.getMessage());
        return "error";
    }
}


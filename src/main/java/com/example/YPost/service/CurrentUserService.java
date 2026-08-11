package com.example.YPost.service;

import com.example.YPost.model.User;
import com.example.YPost.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<User> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        return userRepository.findByUsernameIgnoreCase(authentication.getName());
    }

    @Transactional(readOnly = true)
    public User requireCurrentUser(Authentication authentication) {
        return getCurrentUser(authentication)
                .orElseThrow(() -> new IllegalStateException("Kein eingeloggter Benutzer gefunden."));
    }
}


package com.example.YPost.service;

import com.example.YPost.model.User;
import com.example.YPost.repository.PostRepository;
import com.example.YPost.repository.UserRepository;
import com.example.YPost.web.dto.ProfileView;
import com.example.YPost.web.form.RegistrationForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.YPost.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public User register(RegistrationForm form) {
        String normalizedUsername = form.getUsername().trim();
        String normalizedEmail = form.getEmail().trim().toLowerCase();

        if (userRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
            throw new IllegalArgumentException("Der Benutzername ist bereits vergeben.");
        }
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("Die E-Mail-Adresse ist bereits registriert.");
        }

        User user = new User();
        user.setUsername(normalizedUsername);
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        user.setBio(form.getBio() == null ? null : form.getBio().trim());
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getByUsernameOrEmail(String value) {
        return userRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(value, value)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden."));
    }

    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden."));
    }

    @Transactional(readOnly = true)
    public ProfileView buildProfileView(User profileOwner, boolean ownProfile, java.util.List<com.example.YPost.web.dto.PostView> posts) {
        long postCount = postRepository.countByAuthor(profileOwner);
        long totalLikes = postRepository.countTotalLikesForAuthor(profileOwner);
        return new ProfileView(
                profileOwner.getUsername(),
                profileOwner.getEmail(),
                profileOwner.getBio(),
                postCount,
                totalLikes,
                ownProfile,
                posts
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = getByUsernameOrEmail(usernameOrEmail);
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .roles(user.getRole().replace("ROLE_", ""))
                .build();
    }

    public List<User> searchUsers(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return userRepository.searchUsers(query.trim());
    }


}


package com.example.YPost.config;

import com.example.YPost.model.Post;
import com.example.YPost.model.PostLike;
import com.example.YPost.model.User;
import com.example.YPost.repository.PostLikeRepository;
import com.example.YPost.repository.PostRepository;
import com.example.YPost.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed-data:true}")
    private boolean seedDataEnabled;

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedDataEnabled || userRepository.count() > 0) {
            return;
        }

        User alice = createUser(
                "alice",
                "alice@example.com",
                "Password123!",
                "Ich bin Alice und poste über Java, Kaffee und Lernprojekte."
        );
        User bob = createUser(
                "bob",
                "bob@example.com",
                "Password123!",
                "Bob testet Features, schreibt kleine Updates und liked gute Inhalte."
        );

        List<Post> posts = new ArrayList<>();
        posts.add(createPost(alice, "Willkommen bei yPosts! Heute starte ich mein LAP-Vorbereitungsprojekt."));
        posts.add(createPost(alice, "Spring Boot + PostgreSQL + Docker ist ein solides Setup für diese Aufgabe."));
        posts.add(createPost(alice, "Vergiss nie: gute Validierung spart später viele Fehler."));
        posts.add(createPost(alice, "Mein heutiges Ziel: Registrierung, Login und Likes sauber umsetzen."));
        posts.add(createPost(alice, "420 Zeichen pro Post sind genug für kurze, prägnante Gedanken."));

        posts.add(createPost(bob, "Hallo zusammen, Bob hier. Ich teste gerade die Feed-Sortierung nach Likes."));
        posts.add(createPost(bob, "Wenn das Docker-Setup sauber ist, startet die Entwicklung deutlich entspannter."));
        posts.add(createPost(bob, "Profilseiten machen Social-Media-Apps direkt persönlicher."));
        posts.add(createPost(bob, "Like und Unlike sollten immer idempotent und benutzerfreundlich sein."));
        posts.add(createPost(bob, "Testdaten helfen enorm bei der Demo und beim schnellen Prüfen der Features."));

        createLike(posts.get(5), alice);
        createLike(posts.get(6), alice);
        createLike(posts.get(7), alice);
        createLike(posts.get(8), alice);
        createLike(posts.get(9), alice);

        createLike(posts.get(0), bob);
        createLike(posts.get(1), bob);
        createLike(posts.get(2), bob);
        createLike(posts.get(3), bob);
        createLike(posts.get(4), bob);
    }

    private User createUser(String username, String email, String rawPassword, String bio) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setBio(bio);
        return userRepository.save(user);
    }

    private Post createPost(User author, String content) {
        Post post = new Post();
        post.setAuthor(author);
        post.setContent(content);
        return postRepository.save(post);
    }

    private void createLike(Post post, User user) {
        PostLike like = new PostLike();
        like.setPost(post);
        like.setUser(user);
        postLikeRepository.save(like);
    }
}


package com.example.YPost.service;

import com.example.YPost.model.Post;
import com.example.YPost.model.PostLike;
import com.example.YPost.model.User;
import com.example.YPost.repository.PostLikeRepository;
import com.example.YPost.repository.PostRepository;
import com.example.YPost.web.dto.PostView;
import com.example.YPost.web.form.PostForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostService {

    public static final String SORT_NEWEST = "newest";
    public static final String SORT_LIKES = "likes";

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public Post createPost(User author, PostForm form) {
        String content = form.getContent() == null ? "" : form.getContent().trim();
        if (content.isBlank()) {
            throw new IllegalArgumentException("Post darf nicht leer sein.");
        }
        if (content.length() > 420) {
            throw new IllegalArgumentException("Ein Post darf maximal 420 Zeichen lang sein.");
        }

        Post post = new Post();
        post.setAuthor(author);
        post.setContent(content);
        return postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public List<PostView> getFeed(String sort, User currentUser) {
        String normalizedSort = normalizeSort(sort);
        List<Post> posts = SORT_LIKES.equalsIgnoreCase(normalizedSort)
                ? postRepository.findFeedOrderByLikesDesc()
                : postRepository.findAllByOrderByCreatedAtDesc();
        return mapPosts(posts, currentUser);
    }

    @Transactional(readOnly = true)
    public List<PostView> getPostsForProfile(User profileOwner, User currentUser) {
        return mapPosts(postRepository.findAllByAuthorOrderByCreatedAtDesc(profileOwner), currentUser);
    }

    @Transactional
    public void toggleLike(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post wurde nicht gefunden."));

        if (post.getAuthor().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Eigene Posts können nicht geliked werden.");
        }

        postLikeRepository.findByPostAndUser(post, currentUser)
                .ifPresentOrElse(postLikeRepository::delete, () -> {
                    PostLike like = new PostLike();
                    like.setPost(post);
                    like.setUser(currentUser);
                    postLikeRepository.save(like);
                });
    }

    @Transactional(readOnly = true)
    public String normalizeSort(String sort) {
        if (sort == null) {
            return SORT_NEWEST;
        }
        String normalized = sort.toLowerCase(Locale.ROOT);
        return SORT_LIKES.equals(normalized) ? SORT_LIKES : SORT_NEWEST;
    }

    private List<PostView> mapPosts(List<Post> posts, User currentUser) {
        Set<Long> currentUserLikes = currentUser == null
                ? Set.of()
                : postLikeRepository.findLikedPostIdsByUserId(currentUser.getId());

        return posts.stream()
                .map(post -> new PostView(
                        post.getId(),
                        post.getContent(),
                        post.getCreatedAt(),
                        post.getAuthor().getUsername(),
                        post.getLikes().size(),
                        currentUserLikes.contains(post.getId()),
                        currentUser != null && post.getAuthor().getId().equals(currentUser.getId())
                ))
                .toList();
    }
}


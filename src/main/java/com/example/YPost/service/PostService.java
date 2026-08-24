package com.example.YPost.service;

import com.example.YPost.model.Post;
import com.example.YPost.model.PostLike;
import com.example.YPost.model.User;
import com.example.YPost.repository.PostLikeRepository;
import com.example.YPost.repository.PostRepository;
import com.example.YPost.web.dto.PostView;
import com.example.YPost.web.form.PostForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

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
    public void likePost(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post wurde nicht gefunden."));

        var existing = postLikeRepository.findByPostAndUser(post, currentUser);

        if (existing.isPresent()) {
            PostLike like = existing.get();

            if (!like.isDisliked()) {
                postLikeRepository.delete(like);
                return;
            }

            like.setDisliked(false);
            postLikeRepository.save(like);
            return;
        }

        PostLike like = new PostLike();
        like.setPost(post);
        like.setUser(currentUser);
        like.setDisliked(false);
        postLikeRepository.save(like);
    }

    @Transactional
    public void dislikePost(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post wurde nicht gefunden."));

        var existing = postLikeRepository.findByPostAndUser(post, currentUser);

        if (existing.isPresent()) {
            PostLike like = existing.get();

            if (like.isDisliked()) {
                postLikeRepository.delete(like);
                return;
            }

            like.setDisliked(true);
            postLikeRepository.save(like);
            return;
        }

        PostLike dislike = new PostLike();
        dislike.setPost(post);
        dislike.setUser(currentUser);
        dislike.setDisliked(true);
        postLikeRepository.save(dislike);
    }

    @Transactional
    public Post editPost(Long postId, User currentUser, PostForm form) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post wurde nicht gefunden."));

        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Du kannst nur deine eigenen Posts bearbeiten.");
        }

        String content = form.getContent() == null ? "" : form.getContent().trim();
        if (content.isBlank()) {
            throw new IllegalArgumentException("Post darf nicht leer sein.");
        }
        if (content.length() > 420) {
            throw new IllegalArgumentException("Ein Post darf maximal 420 Zeichen lang sein.");
        }

        post.setContent(content);
        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post wurde nicht gefunden."));

        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Du kannst nur deine eigenen Posts löschen.");
        }

        postRepository.delete(post);
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

        return posts.stream()
                .map(post -> {

                    boolean liked = postLikeRepository.existsByPostAndUserAndDislikedFalse(post, currentUser);
                    boolean disliked = postLikeRepository.existsByPostAndUserAndDislikedTrue(post, currentUser);

                    long likeCount = postLikeRepository.countByPostAndDislikedFalse(post);
                    long dislikeCount = postLikeRepository.countByPostAndDislikedTrue(post);

                    return new PostView(
                            post.getId(),
                            post.getContent(),
                            post.getCreatedAt(),
                            post.getAuthor().getUsername(),
                            likeCount,
                            dislikeCount,
                            liked,
                            disliked,
                            currentUser != null && post.getAuthor().getId().equals(currentUser.getId())
                    );
                })
                .toList();
    }
    @Transactional(readOnly = true)
    public Page<PostView> getFeedPaged(int page, int size, User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findAllPaged(pageable);

        List<PostView> views = mapPosts(postPage.getContent(), currentUser);

        return new PageImpl<>(views, pageable, postPage.getTotalElements());
    }


}

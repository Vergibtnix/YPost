package com.example.YPost.repository;

import com.example.YPost.model.Post;
import com.example.YPost.model.PostLike;
import com.example.YPost.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByPostAndUser(Post post, User user);

    boolean existsByPostAndUser(Post post, User user);

    long countByPost(Post post);

    @Query("select l.post.id from PostLike l where l.user.id = :userId")
    Set<Long> findLikedPostIdsByUserId(@Param("userId") Long userId);
}



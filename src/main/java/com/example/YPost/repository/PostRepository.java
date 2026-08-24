package com.example.YPost.repository;

import com.example.YPost.model.Post;
import com.example.YPost.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"author", "likes"})
    List<Post> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"author", "likes"})
    List<Post> findAllByAuthorOrderByCreatedAtDesc(User author);

    long countByAuthor(User author);

    @Query("""
            select p from Post p
            order by (
                select count(l)
                from PostLike l
                where l.post = p
            ) desc, p.createdAt desc
            """)
    @EntityGraph(attributePaths = {"author", "likes"})
    List<Post> findFeedOrderByLikesDesc();

    @Query("""
            select coalesce(count(l), 0)
            from PostLike l
            where l.post.author = :author
            """)
    long countTotalLikesForAuthor(@Param("author") User author);

    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    Page<Post> findAllPaged(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.author = :author ORDER BY p.createdAt DESC")
    Page<Post> findByAuthorPaged(@Param("author") User author, Pageable pageable);

}

package org.example.socialmediabackend.repository;

import org.example.socialmediabackend.model.Post;
import org.example.socialmediabackend.model.PostReaction;
import org.example.socialmediabackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {
    Optional<PostReaction> findByPostAndUser(Post post, User user);
    void deleteByPostAndUser(Post post, User user);
}
package org.example.socialmediabackend.service;

import org.example.socialmediabackend.dto.CreatePostDto;
import org.example.socialmediabackend.dto.PostDto;
import org.example.socialmediabackend.dto.UpdatePostDto;
import org.example.socialmediabackend.model.Post;
import org.example.socialmediabackend.model.PostReaction;
import org.example.socialmediabackend.model.User;
import org.example.socialmediabackend.repository.PostReactionRepository;
import org.example.socialmediabackend.repository.PostRepository;
import org.example.socialmediabackend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostReactionRepository postReactionRepository;

    public PostService(
            PostRepository postRepository,
            UserRepository userRepository,
            PostReactionRepository postReactionRepository
    ) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postReactionRepository = postReactionRepository;
    }

    @Transactional
    public PostDto createPost(CreatePostDto createPostDto) {
        User currentUser = getCurrentUser();

        Post post = new Post();
        post.setTitle(createPostDto.getTitle());
        post.setContent(createPostDto.getContent());
        post.setAuthor(currentUser);

        Post savedPost = postRepository.save(post);
        return new PostDto(savedPost);
    }

    @Transactional
    public PostDto updatePost(Long postId, UpdatePostDto updatePostDto) {
        User currentUser = getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only update your own posts");
        }

        if (updatePostDto.getTitle() != null && !updatePostDto.getTitle().isEmpty()) {
            post.setTitle(updatePostDto.getTitle());
        }

        if (updatePostDto.getContent() != null && !updatePostDto.getContent().isEmpty()) {
            post.setContent(updatePostDto.getContent());
        }

        Post updatedPost = postRepository.save(post);
        return getPostDtoWithUserReaction(updatedPost, currentUser);
    }

    @Transactional
    public void deletePost(Long postId) {
        User currentUser = getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own posts");
        }

        postRepository.delete(post);
    }

    public PostDto getPost(Long postId) {
        User currentUser = getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return getPostDtoWithUserReaction(post, currentUser);
    }

    public Page<PostDto> getUserPosts(Long userId, Pageable pageable) {
        User currentUser = getCurrentUser();

        Page<Post> posts = postRepository.findByAuthorId(userId, pageable);

        return posts.map(post -> getPostDtoWithUserReaction(post, currentUser));
    }

    public Page<PostDto> getFeedPosts(Pageable pageable) {
        User currentUser = getCurrentUser();

        List<Long> followingIds = currentUser.getFollowing().stream()
                .map(User::getId)
                .collect(Collectors.toList());

        if (followingIds.isEmpty()) {
            // If user doesn't follow anyone, return empty page
            return Page.empty(pageable);
        }

        Page<Post> posts = postRepository.findFeedPosts(followingIds, pageable);

        return posts.map(post -> getPostDtoWithUserReaction(post, currentUser));
    }

    public Page<PostDto> getExplorePosts(Pageable pageable) {
        User currentUser = getCurrentUser();

        Page<Post> posts = postRepository.findAllByOrderByCreatedAtDesc(pageable);

        return posts.map(post -> getPostDtoWithUserReaction(post, currentUser));
    }

    @Transactional
    public PostDto reactToPost(Long postId, boolean like) {
        User currentUser = getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Optional<PostReaction> existingReaction = postReactionRepository.findByPostAndUser(post, currentUser);

        if (existingReaction.isPresent()) {
            PostReaction reaction = existingReaction.get();

            if (reaction.isLike() == like) {
                postReactionRepository.delete(reaction);
                return getPostDtoWithUserReaction(post, currentUser);
            }
            else {
                reaction.setLike(like);
                postReactionRepository.save(reaction);
                return getPostDtoWithUserReaction(post, currentUser);
            }
        } else {
            PostReaction reaction = new PostReaction();
            reaction.setPost(post);
            reaction.setUser(currentUser);
            reaction.setLike(like);

            postReactionRepository.save(reaction);
            return getPostDtoWithUserReaction(post, currentUser);
        }
    }

    private PostDto getPostDtoWithUserReaction(Post post, User currentUser) {
        Optional<PostReaction> userReaction = postReactionRepository.findByPostAndUser(post, currentUser);

        if (userReaction.isPresent()) {
            return new PostDto(post, userReaction.get().isLike());
        } else {
            return new PostDto(post, null);
        }
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }
}
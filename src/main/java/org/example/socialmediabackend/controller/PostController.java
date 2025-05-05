package org.example.socialmediabackend.controller;

import jakarta.validation.Valid;
import org.example.socialmediabackend.dto.CreatePostDto;
import org.example.socialmediabackend.dto.PostDto;
import org.example.socialmediabackend.dto.UpdatePostDto;
import org.example.socialmediabackend.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody CreatePostDto createPostDto) {
        return ResponseEntity.ok(postService.createPost(createPostDto));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Long postId,
            @RequestBody UpdatePostDto updatePostDto
    ) {
        return ResponseEntity.ok(postService.updatePost(postId, updatePostDto));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostDto>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(postService.getUserPosts(userId, pageable));
    }

    @GetMapping("/feed")
    public ResponseEntity<Page<PostDto>> getFeedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postService.getFeedPosts(pageable));
    }

    @GetMapping("/explore")
    public ResponseEntity<Page<PostDto>> getExplorePosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postService.getExplorePosts(pageable));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<PostDto> likePost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.reactToPost(postId, true));
    }

    @PostMapping("/{postId}/dislike")
    public ResponseEntity<PostDto> dislikePost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.reactToPost(postId, false));
    }
}
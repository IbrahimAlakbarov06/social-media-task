package org.example.socialmediabackend.dto;

import lombok.Data;
import org.example.socialmediabackend.model.Post;

import java.time.LocalDateTime;

@Data
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private UserDto author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likesCount;
    private int dislikesCount;
    private Boolean userReaction;

    public PostDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.author = new UserDto(post.getAuthor());
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.likesCount = post.getLikesCount();
        this.dislikesCount = post.getDislikesCount();
    }

    public PostDto(Post post, Boolean userReaction) {
        this(post);
        this.userReaction = userReaction;
    }
}
package org.example.socialmediabackend.dto;

import lombok.Data;
import org.example.socialmediabackend.model.User;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String name;
    private String surname;
    private String bio;
    private String profilePictureUrl;
    private int followersCount;
    private int followingCount;
    private boolean isFollowing;

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.bio = user.getBio();
        this.profilePictureUrl = user.getProfilePictureUrl();
        this.followersCount = user.getFollowers().size();
        this.followingCount = user.getFollowing().size();
    }

    public UserDto(User user, boolean isFollowing) {
        this(user);
        this.isFollowing = isFollowing;
    }
}
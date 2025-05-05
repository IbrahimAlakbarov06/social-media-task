package org.example.socialmediabackend.dto;

import lombok.Data;

@Data
public class UserUpdateDto {
    private String name;
    private String surname;
    private String bio;
    private String profilePictureUrl;
    private String password;
}
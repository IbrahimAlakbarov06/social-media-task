package org.example.socialmediabackend.service;

import org.example.socialmediabackend.dto.UserDto;
import org.example.socialmediabackend.dto.UserUpdateDto;
import org.example.socialmediabackend.model.User;
import org.example.socialmediabackend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    public Page<UserDto> searchUsers(String searchTerm, Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<User> users = userRepository.searchUsers(searchTerm, pageable);
        return users.map(user -> new UserDto(user, currentUser.isFollowing(user)));
    }

    public Page<UserDto> searchUsersByName(String name, Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<User> users = userRepository.findByNameContainingIgnoreCase(name, pageable);
        return users.map(user -> new UserDto(user, currentUser.isFollowing(user)));
    }

    public Page<UserDto> searchUsersBySurname(String surname, Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<User> users = userRepository.findBySurnameContainingIgnoreCase(surname, pageable);
        return users.map(user -> new UserDto(user, currentUser.isFollowing(user)));
    }

    public Page<UserDto> searchUsersByUsername(String username, Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<User> users = userRepository.findByUsernameContainingIgnoreCase(username, pageable);
        return users.map(user -> new UserDto(user, currentUser.isFollowing(user)));
    }

    public UserDto getUserById(Long userId) {
        User currentUser = getCurrentUser();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserDto(user, currentUser.isFollowing(user));
    }

    public UserDto getCurrentUserProfile() {
        User currentUser = getCurrentUser();
        return new UserDto(currentUser);
    }

    @Transactional
    public UserDto updateUser(UserUpdateDto userUpdateDto) {
        User currentUser = getCurrentUser();

        if (userUpdateDto.getBio() != null) {
            currentUser.setBio(userUpdateDto.getBio());
        }

        if (userUpdateDto.getProfilePictureUrl() != null) {
            currentUser.setProfilePictureUrl(userUpdateDto.getProfilePictureUrl());
        }

        if (userUpdateDto.getName() != null) {
            currentUser.setName(userUpdateDto.getName());
        }

        if (userUpdateDto.getSurname() != null) {
            currentUser.setSurname(userUpdateDto.getSurname());
        }

        if (userUpdateDto.getPassword() != null && !userUpdateDto.getPassword().isEmpty()) {
            if (userUpdateDto.getPassword().length() < 8) {
                throw new RuntimeException("Password must be at least 8 characters long");
            }
            currentUser.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
        }

        User updatedUser = userRepository.save(currentUser);
        return new UserDto(updatedUser);
    }

    @Transactional
    public void deleteCurrentUser() {
        User currentUser = getCurrentUser();
        userRepository.delete(currentUser);
    }

    @Transactional
    public UserDto followUser(Long userId) {
        User currentUser = getCurrentUser();
        User userToFollow = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (currentUser.getId().equals(userId)) {
            throw new RuntimeException("You cannot follow yourself");
        }

        currentUser.follow(userToFollow);
        userRepository.save(currentUser);

        return new UserDto(userToFollow, true);
    }

    @Transactional
    public UserDto unfollowUser(Long userId) {
        User currentUser = getCurrentUser();
        User userToUnfollow = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        currentUser.unfollow(userToUnfollow);
        userRepository.save(currentUser);

        return new UserDto(userToUnfollow, false);
    }

    public List<UserDto> getCurrentUserFollowing(Pageable pageable) {
        User currentUser = getCurrentUser();
        return currentUser.getFollowing().stream()
                .map(user -> new UserDto(user, true))
                .collect(Collectors.toList());
    }

    public List<UserDto> getCurrentUserFollowers(Pageable pageable) {
        User currentUser = getCurrentUser();
        return currentUser.getFollowers().stream()
                .map(user -> new UserDto(user, currentUser.isFollowing(user)))
                .collect(Collectors.toList());
    }

    public List<UserDto> getUserFollowing(Long userId, Pageable pageable) {
        User currentUser = getCurrentUser();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getFollowing().stream()
                .map(followedUser -> new UserDto(followedUser, currentUser.isFollowing(followedUser)))
                .collect(Collectors.toList());
    }

    public List<UserDto> getUserFollowers(Long userId, Pageable pageable) {
        User currentUser = getCurrentUser();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getFollowers().stream()
                .map(follower -> new UserDto(follower, currentUser.isFollowing(follower)))
                .collect(Collectors.toList());
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }
}
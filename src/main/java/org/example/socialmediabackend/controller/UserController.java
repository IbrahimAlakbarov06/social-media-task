package org.example.socialmediabackend.controller;

import org.example.socialmediabackend.dto.UserDto;
import org.example.socialmediabackend.dto.UserUpdateDto;
import org.example.socialmediabackend.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateCurrentUser(@RequestBody UserUpdateDto userUpdateDto) {
        return ResponseEntity.ok(userService.updateUser(userUpdateDto));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser() {
        userService.deleteCurrentUser();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<UserDto>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.searchUsers(query, pageable));
    }

    @GetMapping("/search/name")
    public ResponseEntity<Page<UserDto>> searchUsersByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.searchUsersByName(name, pageable));
    }

    @GetMapping("/search/surname")
    public ResponseEntity<Page<UserDto>> searchUsersBySurname(
            @RequestParam String surname,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.searchUsersBySurname(surname, pageable));
    }

    @GetMapping("/search/username")
    public ResponseEntity<Page<UserDto>> searchUsersByUsername(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.searchUsersByUsername(username, pageable));
    }

    @PostMapping("/{userId}/follow")
    public ResponseEntity<UserDto> followUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.followUser(userId));
    }

    @PostMapping("/{userId}/unfollow")
    public ResponseEntity<UserDto> unfollowUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.unfollowUser(userId));
    }

    @GetMapping("/me/following")
    public ResponseEntity<List<UserDto>> getCurrentUserFollowing(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.getCurrentUserFollowing(pageable));
    }

    @GetMapping("/me/followers")
    public ResponseEntity<List<UserDto>> getCurrentUserFollowers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.getCurrentUserFollowers(pageable));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserDto>> getUserFollowing(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.getUserFollowing(userId, pageable));
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserDto>> getUserFollowers(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.getUserFollowers(userId, pageable));
    }
}
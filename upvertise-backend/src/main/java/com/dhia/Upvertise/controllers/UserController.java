package com.dhia.Upvertise.controllers;

import com.dhia.Upvertise.models.user.User;
import com.dhia.Upvertise.repositories.UserRepository;
import com.dhia.Upvertise.services.CloudinaryService;
import com.dhia.Upvertise.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User userRequest) {
        try {
            User savedUser = userService.createUser(userRequest);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating user: " + e.getMessage());
        }
    }

    @PostMapping("/{userId}/upload-photo")
    public ResponseEntity<String> uploadProfilePhoto(
            @PathVariable Integer userId,
            @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = userService.updateProfilePhoto(userId, file);
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading photo: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}

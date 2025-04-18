package com.dhia.Upvertise.controllers;

import com.dhia.Upvertise.dto.UserUpdateRequest;
import com.dhia.Upvertise.models.user.User;
import com.dhia.Upvertise.repositories.UserRepository;
import com.dhia.Upvertise.services.CloudinaryService;
import com.dhia.Upvertise.services.KeycloakService;
import com.dhia.Upvertise.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User userRequest) {
        try {
            log.info("Received user creation request: {}", userRequest);
            User savedUser = userService.createUser(userRequest);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            log.error("User creation failed for email: {}", userRequest.getEmail(), e); // Detailed log
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }



    @PutMapping("/update-from-keycloak")
    public ResponseEntity<?> updateUserFromKeycloak(@RequestBody User userUpdate) {
        try {
            User updatedUser = userService.updateUserFromKeycloak(userUpdate);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating user: " + e.getMessage());
        }
    }

@PutMapping(value = "/update-in-keycloak", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> updateUserInKeycloak(
        @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
        @RequestPart("profilePhoto") MultipartFile profilePhoto) throws IOException {


        // Call the service to update the user in Keycloak
        keycloakService.updateUserInKeycloak(userUpdateRequest, profilePhoto);

        return ResponseEntity.ok("User successfully updated in Keycloak.");

}

    @DeleteMapping("/delete/{keycloakId}")
    public ResponseEntity<?> deleteUser(@PathVariable String keycloakId) {
        userService.deleteUserEverywhere(keycloakId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-from-keycloak/{userId}")
    public ResponseEntity<?> deleteUserFromKeycloak(@PathVariable String userId) {
        try {
            keycloakService.deleteUserFromKeycloak(userId);
            return ResponseEntity.ok("User successfully deleted from Keycloak.");
        } catch (Exception e) {
            log.error("Failed to delete user in Keycloak: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting user in Keycloak: " + e.getMessage());
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
    @GetMapping("/advertiser-emails")
    public List<String> getAdvertiserEmails() {
        return userRepository.findEmailsByRole("Advertiser");
    }
}

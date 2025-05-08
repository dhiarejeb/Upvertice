package com.dhia.Upvertise.controllers;

import com.dhia.Upvertise.dto.UserResponse;
import com.dhia.Upvertise.dto.UserUpdateRequest;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.models.user.User;
import com.dhia.Upvertise.repositories.UserRepository;
import com.dhia.Upvertise.services.CloudinaryService;
import com.dhia.Upvertise.services.KeycloakService;
import com.dhia.Upvertise.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

import static org.keycloak.models.utils.RoleUtils.hasRole;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;
    private final UserService userService;


    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> getUsers(
            Authentication connectedUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String userId = connectedUser.getName();
        boolean isAdmin = connectedUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_Admin"));

        Pageable pageable = PageRequest.of(page, size);

        PageResponse<UserResponse> response = isAdmin
                ? userService.getAllUsers(pageable)
                : userService.getUserById(userId);

        return ResponseEntity.ok(response);
    }


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
    @Operation(summary = "Update a user's information in Keycloak")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user in Keycloak"),
            @ApiResponse(responseCode = "500", description = "Error updating user")
    })

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
    @Operation(summary = "Delete a user from Keycloak")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted user from Keycloak"),
            @ApiResponse(responseCode = "500", description = "Error deleting user")
    })
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
    @GetMapping("/admin-emails")
    public List<String> getAdminEmails() {
        return userRepository.findEmailsByRole("Admin");
    }
    @GetMapping("/provider-emails")
    public List<String> getProviderEmails() {
        return userRepository.findEmailsByRole("Provider");
    }
    @GetMapping("/supplier-emails")
    public List<String> getSupplierEmails() {
        return userRepository.findEmailsByRole("Supplier");
    }

}

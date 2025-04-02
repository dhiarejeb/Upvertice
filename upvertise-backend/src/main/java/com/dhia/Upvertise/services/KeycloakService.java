package com.dhia.Upvertise.services;

import com.dhia.Upvertise.config.KeycloakConfig;
import com.dhia.Upvertise.dto.UserUpdateRequest;
import com.dhia.Upvertise.models.user.User;

import com.dhia.Upvertise.repositories.UserRepository;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakService {
    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    private final Keycloak keycloak;

    private final RestTemplate restTemplate;
    private final CloudinaryService cloudinaryService ;
    private final UserRepository userRepository;







    /**
     * Creates a new user in Keycloak only if they do not already exist.
     */
    public String createUserInKeycloak(User userRequest) {
        // Check if user already exists in Keycloak
        Optional<String> existingUserId = findUserIdByEmail(userRequest.getEmail());
        if (existingUserId.isPresent()) {
            return existingUserId.get(); // Return existing Keycloak ID
        }

        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername(userRequest.getEmail()); // Using email as username
        userRep.setFirstName(userRequest.getFirstName());
        userRep.setLastName(userRequest.getLastName());
        userRep.setEmail(userRequest.getEmail());
        userRep.setEnabled(true);
        userRep.setEmailVerified(true);
        userRep.setAttributes(Collections.singletonMap("profilePhotoUrl", Collections.singletonList(userRequest.getProfilePhotoUrl())));

        // Set password (optional, depends on your needs)
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue("DefaultPassword123!"); // You can change this logic
        passwordCred.setTemporary(true); // User will have to reset password on first login

        userRep.setCredentials(Collections.singletonList(passwordCred));

        // Create user in Keycloak
        Response response = keycloak.realm(realm).users().create(userRep);
        if (response.getStatus() == 201) {
            // Extract user ID from Location header
            String location = response.getHeaderString("Location");
            String userId = location.substring(location.lastIndexOf('/') + 1);
            return userId;
        } else {
            throw new RuntimeException("Failed to create user in Keycloak: " + response.getStatus());
        }
    }

    /**
     * Finds a Keycloak user ID by email.
     */
    private Optional<String> findUserIdByEmail(String email) {
        return keycloak.realm(realm).users().search(email).stream()
                .filter(user -> email.equals(user.getEmail()))
                .map(UserRepresentation::getId)
                .findFirst();
    }

//    public void updateUserInKeycloak(UserUpdateRequest userUpdateRequest) {
//        // Dynamically fetch the realm
//
//        String keycloakUserUrl = serverUrl
//                + "/admin/realms/" + realm + "/users/" + userUpdateRequest.getKeycloakId();
//
//        // Prepare the attributes map, only adding profile photo URL if present
//        Map<String, Object> attributes = new HashMap<>();
//        if (userUpdateRequest.getProfilePhotoUrl() != null) {
//            attributes.put("profilePhotoUrl", Collections.singletonList(userUpdateRequest.getProfilePhotoUrl()));
//        }
//        if (userUpdateRequest.getRole() != null) {
//            attributes.put("role", Collections.singletonList(userUpdateRequest.getRole())); // Ensure role is included
//        }
//
//
//        // Create the body for the update request
//        Map<String, Object> updateBody = new HashMap<>();
//        updateBody.put("firstName", userUpdateRequest.getFirstName());
//        updateBody.put("lastName", userUpdateRequest.getLastName());
//        updateBody.put("email", userUpdateRequest.getEmail());
//        updateBody.put("attributes", attributes);
//
//        // Set up the request headers with the admin access token
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(getAdminAccessToken());
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        // Send the PUT request to update the user
//        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(updateBody, headers);
//        ResponseEntity<String> response = restTemplate.exchange(keycloakUserUrl, HttpMethod.PUT, requestEntity, String.class);
//
//        // Check if the response is successful
//        if (!response.getStatusCode().is2xxSuccessful()) {
//            throw new RuntimeException("Failed to update user in Keycloak. Status: " + response.getStatusCode());
//        }
//        // After updating, assign role
//        assignRoleToUser(userUpdateRequest.getKeycloakId(), userUpdateRequest.getRole());
//
//        log.info("Successfully updated user in Keycloak: {}", userUpdateRequest.getKeycloakId());
//    }

    public void updateUserInKeycloak(UserUpdateRequest userUpdateRequest, MultipartFile profilePhoto) throws IOException {
        // Upload the profile photo to Cloudinary if present
        String uploadedPhotoUrl = null;
        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            uploadedPhotoUrl = cloudinaryService.uploadImage(profilePhoto);
        }

        // Dynamically fetch the realm
        String keycloakUserUrl = serverUrl
                + "/admin/realms/" + realm + "/users/" + userUpdateRequest.getKeycloakId();

        // Prepare the attributes map, only adding profile photo URL if present
        Map<String, Object> attributes = new HashMap<>();
        if (uploadedPhotoUrl != null) {
            attributes.put("profilePhotoUrl", Collections.singletonList(uploadedPhotoUrl));
        }
        if (userUpdateRequest.getRole() != null) {
            attributes.put("role", Collections.singletonList(userUpdateRequest.getRole())); // Ensure role is included
        }

        // Create the body for the update request
        Map<String, Object> updateBody = new HashMap<>();
        updateBody.put("firstName", userUpdateRequest.getFirstName());
        updateBody.put("lastName", userUpdateRequest.getLastName());
        updateBody.put("email", userUpdateRequest.getEmail());
        updateBody.put("attributes", attributes);

        // Set up the request headers with the admin access token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAdminAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send the PUT request to update the user
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(updateBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(keycloakUserUrl, HttpMethod.PUT, requestEntity, String.class);

        // Check if the response is successful
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to update user in Keycloak. Status: " + response.getStatusCode());
        }

        // After updating, assign role
        assignRoleToUser(userUpdateRequest.getKeycloakId(), userUpdateRequest.getRole());

        // Update the user profile photo URL in your local database (PostgreSQL)
        User user = userRepository.findByKeycloakId(userUpdateRequest.getKeycloakId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user != null) {
            user.setProfilePhotoUrl(uploadedPhotoUrl);
            userRepository.save(user);  // Save the updated user entity
        }

        log.info("Successfully updated user in Keycloak: {}", userUpdateRequest.getKeycloakId());
    }

    private String getAdminAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                serverUrl + "/realms/" + realm + "/protocol/openid-connect/token",
                request,
                Map.class
        );

        if (!response.getStatusCode().is2xxSuccessful() || !response.getBody().containsKey("access_token")) {
            throw new RuntimeException("Failed to fetch Keycloak admin access token");
        }

        return response.getBody().get("access_token").toString();
    }

    private void assignRoleToUser(String keycloakUserId, String roleName) {
        String keycloakRoleUrl = serverUrl
                + "/admin/realms/Upvertice/users/" + keycloakUserId + "/role-mappings/realm";

        // Fetch role from Keycloak
        String getRolesUrl = serverUrl + "/admin/realms/Upvertice/roles";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAdminAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<RoleRepresentation[]> response = restTemplate.exchange(
                getRolesUrl, HttpMethod.GET, new HttpEntity<>(headers), RoleRepresentation[].class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to fetch roles from Keycloak");
        }

        RoleRepresentation[] roles = response.getBody();
        Optional<RoleRepresentation> role = Arrays.stream(roles)
                .filter(r -> r.getName().equals(roleName))
                .findFirst();

        if (!role.isPresent()) {
            throw new RuntimeException("Role " + roleName + " not found in Keycloak");
        }

        // Assign role to user
        HttpEntity<List<RoleRepresentation>> requestEntity = new HttpEntity<>(Collections.singletonList(role.get()), headers);
        ResponseEntity<String> roleResponse = restTemplate.exchange(keycloakRoleUrl, HttpMethod.POST, requestEntity, String.class);

        if (!roleResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to assign role to user in Keycloak: " + roleResponse.getBody());
        }
    }

    public void deleteUserFromKeycloak(String keycloakUserId) {
        String keycloakUserUrl = serverUrl + "/admin/realms/" + realm + "/users/" + keycloakUserId;

        // Get Admin Access Token
        String adminAccessToken = getAdminAccessToken();

        // Step 1: Check if User Exists
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminAccessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> userResponse = restTemplate.exchange(
                keycloakUserUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        if (userResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new RuntimeException("User not found in Keycloak.");
        }

        // Step 2: Remove Roles Assigned to User
        removeUserRoles(keycloakUserId, adminAccessToken);

        // Step 3: Delete User from Keycloak
        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                keycloakUserUrl, HttpMethod.DELETE, new HttpEntity<>(headers), String.class);

        if (!deleteResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to delete user in Keycloak. Status: " + deleteResponse.getStatusCode());
        }

        log.info("User {} successfully deleted from Keycloak.", keycloakUserId);
    }

    private void removeUserRoles(String keycloakUserId, String adminAccessToken) {
        String keycloakRoleUrl = serverUrl + "/admin/realms/" + realm + "/users/" + keycloakUserId + "/role-mappings/realm";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminAccessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Fetch all assigned roles
        ResponseEntity<RoleRepresentation[]> response = restTemplate.exchange(
                keycloakRoleUrl, HttpMethod.GET, new HttpEntity<>(headers), RoleRepresentation[].class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to fetch user roles from Keycloak.");
        }

        RoleRepresentation[] roles = response.getBody();
        if (roles != null && roles.length > 0) {
            // Remove all assigned roles
            HttpEntity<List<RoleRepresentation>> requestEntity = new HttpEntity<>(Arrays.asList(roles), headers);
            restTemplate.exchange(keycloakRoleUrl, HttpMethod.DELETE, requestEntity, String.class);
        }

        log.info("All roles removed for user {} before deletion.", keycloakUserId);
    }
}

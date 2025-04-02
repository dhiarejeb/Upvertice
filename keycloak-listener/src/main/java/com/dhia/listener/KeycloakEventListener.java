package com.dhia.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class KeycloakEventListener implements EventListenerProvider {
    private final KeycloakSession session;
    private final HttpClient httpClient;

    private final ObjectMapper objectMapper;

    public KeycloakEventListener(KeycloakSession session , HttpClient httpClient) {
        this.session = session;
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void onEvent(Event event) {
        String userId = event.getUserId();
        RealmModel realm = session.getContext().getRealm();
        UserModel user = session.users().getUserById(realm, userId);

        switch (event.getType()) {
            case REGISTER:
                handleUserRegistration(userId, user, realm);
                break;

            case UPDATE_PROFILE:
                handleUserUpdate(userId, user);
                break;

            case DELETE_ACCOUNT:
                handleUserDeletion(userId);
                break;

            default:
                break;
        }
    }

    private void handleUserRegistration(String userId, UserModel user, RealmModel realm) {
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String email = user.getEmail();
        String selectedRole = user.getFirstAttribute("role");

        if (selectedRole != null) {
            RoleModel roleModel = realm.getRole(selectedRole);
            if (roleModel != null) {
                user.grantRole(roleModel);
            }
        }

        sendUserToBackend(userId, firstName, lastName, email, selectedRole);
    }

    private void handleUserUpdate(String userId, UserModel user) {
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String email = user.getEmail();
        String role = user.getFirstAttribute("role"); // Get role from Keycloak

        Map<String, String> updateData = new HashMap<>();
        updateData.put("keycloakId", userId);
        updateData.put("firstName", firstName);
        updateData.put("lastName", lastName);
        updateData.put("email", email);
        updateData.put("role", role); // Add role to the payload

        sendUpdateToBackend(updateData);
    }

    private void sendUpdateToBackend(Map<String, String> updateData) {
        try {
            String backendUrl = "http://host.docker.internal:8088/api/v1/users/update-from-keycloak";
            String requestBody = objectMapper.writeValueAsString(updateData);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(backendUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + getAdminToken())
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("User updated in backend: " + response.statusCode());
        } catch (Exception e) {
            log.error("Error updating user in backend", e);
        }
    }

    private void handleUserDeletion(String userId) {
        try {
            String backendUrl = "http://host.docker.internal:8088/api/v1/users/delete/" + userId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(backendUrl))
                    .header("Authorization", "Bearer " + getAdminToken())
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("User deleted in backend: " + response.statusCode());
        } catch (Exception e) {
            log.error("Error deleting user from backend", e);
        }
    }

    private void sendUserToBackend(String userId, String firstName, String lastName, String email, String role) {
        //String backendUrl = "http://host.docker.internal:8088/api/v1/users"; // Pointing to the backend running on the host machine
        String HOST = System.getenv("KEYCLOAK_HOST") != null ? System.getenv("KEYCLOAK_HOST") : "localhost";
        String backendUrl = "http://host.docker.internal:8088/api/v1/users";


        Map<String, String> userData = new HashMap<>();
        userData.put("keycloakId", userId);
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);
        userData.put("email", email);
        userData.put("role", role);
        userData.put("profilePhotoUrl", "https://default-image.com/default-profile.png");

        try {
            String requestBody = objectMapper.writeValueAsString(userData);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(backendUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + getAdminToken())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("User successfully sent to backend: " + response.statusCode());
        } catch (Exception e) {
            log.error("Error sending user data to backend", e);
        }
    }

    private String getAdminToken() {
        String HOST = System.getenv("KEYCLOAK_HOST") != null ? System.getenv("KEYCLOAK_HOST") : "localhost";
        //String tokenUrl = "http://host.docker.internal:9090/realms/Upvertice/protocol/openid-connect/token";
        String tokenUrl = "http://localhost:8080/realms/Upvertice/protocol/openid-connect/token";
        String clientId = "Upvertice-rest-api"; //Upvertice-rest-api
        String clientSecret = "n87pRqxXd6ddkDbURfsoOQ12glEeuR9E";

        String requestBody = "client_id=" + clientId + "&client_secret=" + clientSecret + "&grant_type=client_credentials";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(tokenUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> responseBody = objectMapper.readValue(response.body(), Map.class);
            return (String) responseBody.get("access_token");
        } catch (Exception e) {
            log.error("Token retrieval error", e);

            return null;
        }
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {
        // Handle ADMIN-initiated user deletions
        if (adminEvent.getOperationType() == OperationType.DELETE
                && adminEvent.getResourceType() == ResourceType.USER) {

            // Extract user ID from resource path (format: "users/{userId}")
            String resourcePath = adminEvent.getResourcePath();
            String userId = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);

            log.info("Processing admin deletion for user: {}", userId);
            handleUserDeletion(userId);
        }
        // Check for USER update events
        else if (adminEvent.getOperationType() == OperationType.UPDATE
                && adminEvent.getResourceType() == ResourceType.USER) {

            // Extract user ID from resource path (e.g., "users/a1b2c3d4")
            String resourcePath = adminEvent.getResourcePath();
            String userId = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);

            RealmModel realm = session.getContext().getRealm();
            UserModel user = session.users().getUserById(realm, userId);

            handleUserUpdate(userId, user);
        }
    }

    @Override
    public void close() {
    }




}

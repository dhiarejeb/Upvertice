package com.dhia.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
//import okhttp3.*;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;


@Slf4j
public class KeycloakEventListener implements EventListenerProvider {
    private final KeycloakSession session;
    //private final OkHttpClient httpClient;
    //private final ObjectMapper objectMapper;
    //private static final String BACKEND_URL = "http://localhost:8088/api/v1"; // Your backend URL

    public KeycloakEventListener(KeycloakSession session) {
        this.session = session;
        //this.httpClient = new OkHttpClient();
        //this.objectMapper = new ObjectMapper();
    }
    @Override
    public void onEvent(Event event) {
        if (event.getType() == EventType.REGISTER) {
            String userId = event.getUserId();
            RealmModel realm = session.getContext().getRealm(); // Get the realm

            // Use the correct method to retrieve the user by ID
            UserModel user = session.users().getUserById(realm ,userId);

            // Get the custom role attribute from the user's profile
            String selectedRole = user.getFirstAttribute("role"); // "role" is the custom attribute

            if (selectedRole != null) {
                // Directly assign the appropriate role from the realm roles
                RoleModel roleModel = realm.getRole(selectedRole);

                if (roleModel != null) {
                    user.grantRole(roleModel);
                }
            }
        }
    }

    /*@Override
    public void onEvent(Event event) {
        try {
            log.info("Event Occurred: {}", event.getType());

            switch (event.getType()) {
                case REGISTER:
                    handleRegistration(event);
                    break;
                case LOGIN:
                    handleLogin(event);
                    break;
                case UPDATE_PROFILE:
                    handleProfileUpdate(event);
                    break;
            }
        } catch (Exception e) {
            log.error("Error processing event", e);
        }
    }

    private void handleRegistration(Event event) {
        try {
            UserModel user = session.users().getUserById(session.getContext().getRealm(), event.getUserId());
            if (user != null) {
                UserEventDTO userEvent = UserEventDTO.builder()
                        .userId(user.getId())
                        .email(user.getEmail())
                        .username(user.getUsername())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .eventType("REGISTER")
                        .timestamp(event.getTime())
                        .build();

                sendToBackend("/api/v1/users/register", userEvent);
            }
        } catch (Exception e) {
            log.error("Error handling registration", e);
        }
    }

    private void handleLogin(Event event) {
        try {
            UserModel user = session.users().getUserById(session.getContext().getRealm(), event.getUserId());
            if (user != null) {
                UserEventDTO userEvent = UserEventDTO.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .eventType("LOGIN")
                        .timestamp(event.getTime())
                        .build();

                sendToBackend("/api/v1/users/login", userEvent);
            }
        } catch (Exception e) {
            log.error("Error handling login", e);
        }
    }

    private void handleProfileUpdate(Event event) {
        try {
            UserModel user = session.users().getUserById(session.getContext().getRealm(), event.getUserId());
            if (user != null) {
                UserEventDTO userEvent = UserEventDTO.builder()
                        .userId(user.getId())
                        .email(user.getEmail())
                        .username(user.getUsername())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .eventType("UPDATE_PROFILE")
                        .timestamp(event.getTime())
                        .build();

                sendToBackend("/api/v1/users/update", userEvent);
            }
        } catch (Exception e) {
            log.error("Error handling profile update", e);
        }
    }

    private void sendToBackend(String path, UserEventDTO userEvent) {
        try {
            String json = objectMapper.writeValueAsString(userEvent);
            RequestBody body = RequestBody.create(json,
                    MediaType.parse("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(BACKEND_URL + path)
                    .post(body)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Failed to send event to backend: {}", response.body().string());
                }
            }
        } catch (Exception e) {
            log.error("Error sending event to backend", e);
        }
    }*/

    @Override
    public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {
        // Handle admin events if needed
    }

    @Override
    public void close() {
    }













//    private final KeycloakSession keycloakSession;
//
//    public KeycloakEventListener(KeycloakSession session) {
//        this.keycloakSession= session;
//    }
//    private static final Logger log = LoggerFactory.getLogger(KeycloakEventListener.class);
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    @Override
//    public void onEvent(Event event) {
//        log.info("Keycloak Event: Type={} UserId={}", event.getType(), event.getUserId());
//
//    }
//
//    @Override
//    public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {
//        log.info("Keycloak Admin Event: Type={} Resource={}", adminEvent.getOperationType(), adminEvent.getResourcePath());
//    }
//
//    @Override
//    public void close() {
//        log.info("Closing KeycloakEventListener...");
//    }
}

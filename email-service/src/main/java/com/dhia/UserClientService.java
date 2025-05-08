package com.dhia;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserClientService {
    private final RestTemplate restTemplate;
    private final String ADVERTISER_EMAILS_URL = "http://localhost:8088/api/v1/users/advertiser-emails";
    private final String ADMIN_EMAILS_URL = "http://localhost:8088/api/v1/users/admin-emails";
    private final String PROVIDER_EMAILS_URL = "http://localhost:8088/api/v1/users/provider-emails";
    private final String SUPPLIER_EMAILS_URL = "http://localhost:8088/api/v1/users/supplier-emails";

    private final String KEYCLOAK_TOKEN_URL = "http://localhost:9090/realms/Upvertice/protocol/openid-connect/token"; //http://localhost:8080/realms/Upvertice/protocol/openid-connect/token
    private final String CLIENT_ID = "email-service";
    private final String CLIENT_SECRET = "Dj6a5rADW5Ypc0sKKK0H7ByJv1VkP9Ko"; // Replace with actual secret
    private final String GRANT_TYPE = "client_credentials";

    /*public List<String> getAdvertiserEmails() {
        String token = getAccessToken(); // Step 1: Get Keycloak Token

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token); // Step 2: Add Token to Request
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<String>> response = restTemplate.exchange(
                USER_SERVICE_URL,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }*/
    // Generalized method to fetch emails
    private List<String> fetchEmails(String serviceUrl) {
        String token = getAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<String>> response = restTemplate.exchange(
                serviceUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }
    public List<String> getAdvertiserEmails() {
        return fetchEmails(ADVERTISER_EMAILS_URL);
    }

    public List<String> getAdminEmails() {
        return fetchEmails(ADMIN_EMAILS_URL);
    }

    public List<String> getProviderEmails() {
        return fetchEmails(PROVIDER_EMAILS_URL);
    }

    public List<String> getSupplierEmails() {
        return fetchEmails(SUPPLIER_EMAILS_URL);
    }
    private String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "client_id=" + CLIENT_ID +
                "&client_secret=" + CLIENT_SECRET +
                "&grant_type=" + GRANT_TYPE;

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                KEYCLOAK_TOKEN_URL,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {}
        );

        return (String) response.getBody().get("access_token");
    }




}

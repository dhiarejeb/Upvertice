package com.dhia.Upvertise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String profilePhotoUrl;
}

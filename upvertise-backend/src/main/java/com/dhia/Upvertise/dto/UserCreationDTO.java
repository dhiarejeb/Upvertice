package com.dhia.Upvertise.dto;

public record UserCreationDTO(
        String keycloakId,
        String firstName,
        String lastName,
        String email,
        String role,
        String profilePhotoUrl
) {
}

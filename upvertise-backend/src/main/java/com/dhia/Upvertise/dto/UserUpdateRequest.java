package com.dhia.Upvertise.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    @NotBlank(message = "Keycloak ID is required")
    private String keycloakId;

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    //@Size(max = 255, message = "Profile photo URL cannot exceed 255 characters")
    private String profilePhotoUrl;  // Optional field

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "ADMIN|ADVERTISER|SUPPLIER|PROVIDER", message = "Invalid role. Allowed roles: Admin, Advertiser, Supplier, Provider")
    private String role;
}

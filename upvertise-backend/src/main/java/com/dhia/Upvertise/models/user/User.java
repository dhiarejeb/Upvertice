package com.dhia.Upvertise.models.user;


import com.dhia.Upvertise.models.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "_user")
public class User extends BaseEntity  {
    @Column(unique = true, nullable = false) // Keycloak UUID
    private String keycloakId;

    private String firstName;
    private String lastName;
    @Column(unique = true, nullable = false)
    private String email;
    private String role;
    private String profilePhotoUrl;


}

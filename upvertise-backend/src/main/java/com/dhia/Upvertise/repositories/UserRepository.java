package com.dhia.Upvertise.repositories;

import com.dhia.Upvertise.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u.email FROM User u WHERE u.role = 'Advertiser'")
    List<String> findEmailsByRole(String role);

    Optional<User> findByKeycloakId(String keycloakId); // Changed from findByEmail
}

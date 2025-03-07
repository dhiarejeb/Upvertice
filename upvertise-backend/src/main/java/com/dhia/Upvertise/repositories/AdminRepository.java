package com.dhia.Upvertise.repositories;

import com.dhia.Upvertise.models.user.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository { //extends JpaRepository<Admin, String> {
    Optional<Admin> findByEmail(String username);
}

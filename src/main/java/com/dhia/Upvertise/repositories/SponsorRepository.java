package com.dhia.Upvertise.repositories;

import com.dhia.Upvertise.models.user.Sponsor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SponsorRepository {//extends JpaRepository<Sponsor, String> {
    Optional<Sponsor> findById(Integer sponsorId);

    Optional<Sponsor> findByEmail(String email);
}

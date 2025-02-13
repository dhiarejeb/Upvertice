package com.dhia.Upvertise.repositories;

import com.dhia.Upvertise.models.sponsorship.SponsorAd;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SponsorAdRepository extends JpaRepository<SponsorAd, Integer> {
    Page<SponsorAd> findByCreatedBy(String userId, Pageable pageable);
}

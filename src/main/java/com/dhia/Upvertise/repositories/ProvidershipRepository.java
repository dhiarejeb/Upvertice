package com.dhia.Upvertise.repositories;

import com.dhia.Upvertise.models.provider.Providership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvidershipRepository extends JpaRepository<Providership, Integer> {
    Page<Providership> findByUserId(String userId, Pageable pageable);
}

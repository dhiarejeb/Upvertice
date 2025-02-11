package com.dhia.Upvertise.repositories;

import com.dhia.Upvertise.models.sponsorship.SponsorOffer;
import com.dhia.Upvertise.models.sponsorship.SponsorOfferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SponsorOfferRepository extends JpaRepository<SponsorOffer, Integer > {


    Page<SponsorOffer> findByStatus(SponsorOfferStatus status, Pageable pageable);
}

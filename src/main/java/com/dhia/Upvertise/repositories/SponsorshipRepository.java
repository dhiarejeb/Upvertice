package com.dhia.Upvertise.repositories;

import com.dhia.Upvertise.models.sponsorship.SponsorAd;
import com.dhia.Upvertise.models.sponsorship.SponsorOffer;
import com.dhia.Upvertise.models.sponsorship.Sponsorship;
import com.dhia.Upvertise.models.sponsorship.SponsorshipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface SponsorshipRepository extends JpaRepository<Sponsorship, Integer> {


    Page<Sponsorship> findByCreatedBy(String createdBy , Pageable pageable);
    Page<Sponsorship> findByStatus(SponsorshipStatus status , Pageable pageable);
    Page<Sponsorship> findByStatusAndCreatedBy(SponsorshipStatus status, String createdBy,Pageable pageable);
    @Query("""
    SELECT s 
    FROM Sponsorship s JOIN s.sponsorAds ads 
    WHERE s.userId = :userId AND ads.id = :sponsorAdId""")
    List<Sponsorship> findByUserIdAndSponsorAdId(@Param("userId") String userId, @Param("sponsorAdId") Integer sponsorAdId);


    @Query("""
    SELECT s FROM Sponsorship s
    WHERE s.sponsorOffer.id = :sponsorOfferId 
    AND s.userId = :userId
""")
    Optional<Sponsorship> findBySponsorOfferIdAndUserId(@Param("sponsorOfferId") Integer sponsorOfferId,
                                                        @Param("userId") String userId);

    List<Sponsorship> findBySponsorOffer(SponsorOffer sponsorOffer);


    List<Sponsorship> findBySponsorAdsContaining(SponsorAd sponsorAd);



}

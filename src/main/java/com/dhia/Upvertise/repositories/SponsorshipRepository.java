package com.dhia.Upvertise.repositories;

import com.dhia.Upvertise.models.sponsorship.Sponsorship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface SponsorshipRepository extends JpaRepository<Sponsorship, Integer> {


    //@Query("""
    //SELECT s FROM Sponsorship s WHERE s.sponsor_id = :sponsorId AND s.sponsorad_id = :adId""")
    //Optional<Sponsorship> findBySponsorIdAndSponsorAdId(Integer sponsorId, Integer adId);
    @Query("""
    SELECT s 
    FROM Sponsorship s JOIN s.sponsorAds ads 
    WHERE s.userId = :userId AND ads.id = :sponsorAdId""")
    Optional<Sponsorship> findByUserIdAndSponsorAdId(@Param("userId") String userId, @Param("sponsorAdId") Integer sponsorAdId);


    @Query("""
    SELECT s FROM Sponsorship s
    WHERE s.sponsorOffer.id = :sponsorOfferId 
    AND s.userId = :userId
""")
    Optional<Sponsorship> findBySponsorOfferIdAndUserId(@Param("sponsorOfferId") Integer sponsorOfferId,
                                                        @Param("userId") String userId);
//    @Query("""
//SELECT s
//FROM Sponsorship s
//WHERE s.sponsorOffer.id = :oldOfferId
//""")
//    Optional<Sponsorship> findBySponsorOfferId(Integer oldOfferId);
//
}

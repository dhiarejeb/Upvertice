package com.dhia.Upvertise.services;

import com.dhia.Upvertise.dto.SponsorAdRequest;
import com.dhia.Upvertise.dto.SponsorAdResponse;
import com.dhia.Upvertise.mapper.SponsorAdMapper;
import com.dhia.Upvertise.models.sponsorship.SponsorAd;
import com.dhia.Upvertise.models.sponsorship.Sponsorship;
import com.dhia.Upvertise.models.sponsorship.SponsorshipStatus;
import com.dhia.Upvertise.repositories.SponsorAdRepository;
import com.dhia.Upvertise.repositories.SponsorshipRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SponsorAdService {


    private final SponsorAdRepository sponsorAdRepository;
    private final SponsorshipRepository sponsorshipRepository;




    @Transactional
    public SponsorAdResponse updateSponsorAd(Authentication connectedUser, Integer adId, SponsorAdRequest request) {

        // Retrieve SponsorAd
        SponsorAd sponsorAd = sponsorAdRepository.findById(adId)
                .orElseThrow(() -> new EntityNotFoundException("Sponsor Ad not found"));

        // Ensure the ad belongs to the sponsor
        if (!sponsorAd.getCreatedBy().equals(connectedUser.getName())) {
            throw new AccessDeniedException("You can only update your own ads");
        }

        // Fetch related sponsorship

        Sponsorship sponsorship = sponsorshipRepository.findByUserIdAndSponsorAdId(connectedUser.getName(), adId)
                .orElseThrow(() -> new EntityNotFoundException("No sponsorship found for this ad"));

        // Business Rule: Only allow updates if sponsorship is PENDING or REJECTED
        if (sponsorship.getStatus() != SponsorshipStatus.PENDING
                && sponsorship.getStatus() != SponsorshipStatus.REJECTED) {
            throw new IllegalStateException("You cannot update an ad for an approved or completed sponsorship");
        }

        // Apply changes
        if (request.content() != null) sponsorAd.setContent(request.content());
        if (request.design() != null) sponsorAd.setDesign(request.design());
        if (request.designColors() != null) sponsorAd.setDesign_colors(request.designColors());

        sponsorAdRepository.save(sponsorAd);
        return SponsorAdMapper.toSponsorAdResponse(sponsorAd);
    }
}

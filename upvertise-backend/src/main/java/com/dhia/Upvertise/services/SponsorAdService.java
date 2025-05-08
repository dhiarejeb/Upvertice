package com.dhia.Upvertise.services;

import com.dhia.Upvertise.dto.SponsorAdRequest;
import com.dhia.Upvertise.dto.SponsorAdResponse;
import com.dhia.Upvertise.mapper.SponsorAdMapper;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.models.sponsorship.SponsorAd;
import com.dhia.Upvertise.models.sponsorship.Sponsorship;
import com.dhia.Upvertise.models.sponsorship.SponsorshipStatus;
import com.dhia.Upvertise.repositories.SponsorAdRepository;
import com.dhia.Upvertise.repositories.SponsorshipRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SponsorAdService {


    private final SponsorAdRepository sponsorAdRepository;
    private final SponsorshipRepository sponsorshipRepository;
    private final CloudinaryService cloudinaryService;
    private final SponsorAdMapper sponsorAdMapper;



    @Cacheable(value = "sponsorAds", key = "#connectedUser.name + '-' + #page + '-' + #size")
    public PageResponse<SponsorAdResponse> getAllSponsorAds(Authentication connectedUser, int page, int size) {
        String userId = connectedUser.getName(); // Assuming Keycloak ID is stored here

        // Check if the user is an Admin
        boolean isAdmin = connectedUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_Admin"));
        boolean isProvider = connectedUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_Provider"));

        Pageable pageable = PageRequest.of(page, size);
        Page<SponsorAd> sponsorAdsPage;

        // Fetch based on user role
        if (isAdmin || isProvider) {
            sponsorAdsPage = sponsorAdRepository.findAll(pageable);
        } else {
            sponsorAdsPage = sponsorAdRepository.findByCreatedBy(userId, pageable);
        }

        // Convert to response with Cloudinary image URLs
        List<SponsorAdResponse> sponsorAdResponses = sponsorAdsPage.getContent().stream()
                .map(sponsorAd -> sponsorAdMapper.toSponsorAdResponseWithImageUrl(sponsorAd))
                .collect(Collectors.toList());

        return new PageResponse<>(
                sponsorAdResponses,
                sponsorAdsPage.getNumber(),
                sponsorAdsPage.getSize(),
                sponsorAdsPage.getTotalElements(),
                sponsorAdsPage.getTotalPages(),
                sponsorAdsPage.isFirst(),
                sponsorAdsPage.isLast()
        );
    }




    @CacheEvict(value = "sponsorAds", allEntries = true)
    public SponsorAdResponse updateSponsorAd(Authentication connectedUser, Integer adId, SponsorAdRequest request , MultipartFile image) {

        // Retrieve SponsorAd
        SponsorAd sponsorAd = sponsorAdRepository.findById(adId)
                .orElseThrow(() -> new EntityNotFoundException("Sponsor Ad not found"));

        // Check if user is an Admin
        boolean isAdmin = connectedUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_Admin"));

        // Ensure the user is allowed to update the ad
        if (!isAdmin && !sponsorAd.getCreatedBy().equals(connectedUser.getName())) {
            throw new AccessDeniedException("You can only update your own ads");
        }

        // Fetch related sponsorships
        List<Sponsorship> sponsorships = sponsorshipRepository.findBySponsorAdsContaining(sponsorAd);

        // ✅ If no sponsorships exist, allow update
        if (sponsorships.isEmpty() ) {
            sponsorAdMapper.updateSponsorAdFromRequest(request, sponsorAd, image);
            sponsorAdRepository.save(sponsorAd);
            return sponsorAdMapper.toSponsorAdResponseWithImageUrl(sponsorAd);
        }

        // ✅ If there are sponsorships, enforce status restrictions
        boolean canUpdate = sponsorships.stream()
                .allMatch(sponsorship -> sponsorship.getStatus() == SponsorshipStatus.PENDING
                        || sponsorship.getStatus() == SponsorshipStatus.REJECTED);

        if (!canUpdate) {
            throw new IllegalStateException("You cannot update an ad for an approved or completed sponsorship");
        }

        // ✅ Use the mapper to update the existing SponsorAd
        sponsorAdMapper.updateSponsorAdFromRequest(request, sponsorAd, image);

        // Save updated entity
        sponsorAdRepository.save(sponsorAd);


        return sponsorAdMapper.toSponsorAdResponseWithImageUrl(sponsorAd);
    }
    @CacheEvict(value = "sponsorAds", allEntries = true)
    public void deleteSponsorAd(Authentication connectedUser, Integer adId) {
        // Retrieve SponsorAd
        SponsorAd sponsorAd = sponsorAdRepository.findById(adId)
                .orElseThrow(() -> new EntityNotFoundException("Sponsor Ad not found"));

        // Ensure the authenticated user is the sponsor or an admin
        boolean isAdmin = connectedUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_Admin"));

        if (!isAdmin && !sponsorAd.getCreatedBy().equals(connectedUser.getName())) {
            throw new AccessDeniedException("You are not authorized to delete this ad");
        }

        // Fetch related Sponsorships
        Set<Sponsorship> sponsorships = sponsorAd.getSponsorships();

        // ✅ If there are no sponsorships, allow deletion
        if (sponsorships.isEmpty()) {
            if (sponsorAd.getDesign() != null && !sponsorAd.getDesign().isBlank()) {
                cloudinaryService.deleteImage(sponsorAd.getDesign());
            }
            sponsorAdRepository.delete(sponsorAd);
            return;
        }

        // Check if any Sponsorship is in an invalid state
        boolean canDelete = sponsorships.stream()
                .allMatch(sponsorship -> sponsorship.getStatus() == SponsorshipStatus.PENDING
                        || sponsorship.getStatus() == SponsorshipStatus.REJECTED);

        if (!canDelete) {
            throw new IllegalStateException("You cannot delete an ad linked to an approved or completed sponsorship");
        }

        // Remove the links between this ad and all Sponsorships
        sponsorships.forEach(sponsorship -> sponsorship.getSponsorAds().remove(sponsorAd));
        // ✅ Delete associated image if exists
        if (sponsorAd.getDesign() != null && !sponsorAd.getDesign().isBlank()) {
            cloudinaryService.deleteImage(sponsorAd.getDesign());
        }
        // Delete the ad
        sponsorAdRepository.delete(sponsorAd);
    }

}

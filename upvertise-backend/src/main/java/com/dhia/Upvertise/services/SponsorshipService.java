package com.dhia.Upvertise.services;

import com.dhia.Upvertise.dto.SponsorshipPatchRequest;
import com.dhia.Upvertise.dto.SponsorshipResponse;
import com.dhia.Upvertise.handler.*;
import com.dhia.Upvertise.mapper.SponsorAdMapper;
import com.dhia.Upvertise.mapper.SponsorshipMapper;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.models.sponsorship.SponsorAd;
import com.dhia.Upvertise.models.sponsorship.Sponsorship;
import com.dhia.Upvertise.models.sponsorship.SponsorshipStatus;
import com.dhia.Upvertise.repositories.SponsorAdRepository;
import com.dhia.Upvertise.repositories.SponsorshipRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor

public class SponsorshipService {

    private final SponsorshipRepository sponsorshipRepository;
    private final SponsorAdMapper sponsorAdMapper;
    private final SponsorAdRepository sponsorAdRepository;
    private final CloudinaryService cloudinaryService;



    public PageResponse<SponsorshipResponse> getAllSponsorships(Authentication connectedUser, int page, int size) {

        String userId = connectedUser.getName(); // Assuming Keycloak ID is stored here

        // Check if the user is an Admin
        boolean isAdmin = connectedUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_Admin"));
        Page<Sponsorship> sponsorshipsPage ;
        Pageable pageable = PageRequest.of(page, size);
        if (isAdmin) {
            // Log to see if this path is being entered
            System.out.println("Admin detected, fetching all sponsorships.");
            sponsorshipsPage = sponsorshipRepository.findAll(pageable);
        } else {
            // Log to see if this path is being entered
            sponsorshipsPage = sponsorshipRepository.findByCreatedBy(userId, pageable);
        }
        List<SponsorshipResponse> sponsorshipResponses = sponsorshipsPage
                .getContent()
                .stream()
                .map(SponsorshipMapper::toSponsorshipResponse)
                .toList();

        return new PageResponse<>(
                sponsorshipResponses,
                sponsorshipsPage.getNumber(),
                sponsorshipsPage.getSize(),
                sponsorshipsPage.getTotalElements(),
                sponsorshipsPage.getTotalPages(),
                sponsorshipsPage.isFirst(),
                sponsorshipsPage.isLast()
        );
    }
    public SponsorshipResponse getSponsorshipById(Authentication connectedUser, Integer sponsorshipId){
        String userId = connectedUser.getName();
        boolean isAdmin = connectedUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_Admin"));

        // Fetch sponsorship or throw 404
        Sponsorship sponsorship = sponsorshipRepository.findById(sponsorshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Sponsorship not found"));

        // Authorization check
        if (!isAdmin && !sponsorship.getCreatedBy().equals(userId)) {
            throw new AccessDeniedException("You don't have permission to access this sponsorship");
        }

        return SponsorshipMapper.toSponsorshipResponse(sponsorship);
    }

    public PageResponse<SponsorshipResponse> getSponsorshipsByStatus(SponsorshipStatus status, Authentication connectedUser, int page, int size) {
        // Check if the user is an Admin
        boolean isAdmin = connectedUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_Admin"));

        Pageable pageable = PageRequest.of(page, size);  // Creating Pageable object for pagination

        Page<Sponsorship> sponsorshipsPage;

        if (isAdmin) {
            // Admin can filter all sponsorships by status with pagination
            sponsorshipsPage = sponsorshipRepository.findByStatus(status, pageable);
        } else {
            // Sponsors can only filter their own sponsorships by status with pagination
            sponsorshipsPage = sponsorshipRepository.findByStatusAndCreatedBy(status, connectedUser.getName(), pageable);
        }

        List<SponsorshipResponse> sponsorshipResponses = sponsorshipsPage
                .getContent()
                .stream()
                .map(SponsorshipMapper::toSponsorshipResponse)
                .toList();

        // Return paginated response
        return new PageResponse<>(
                sponsorshipResponses,
                sponsorshipsPage.getNumber(),
                sponsorshipsPage.getSize(),
                sponsorshipsPage.getTotalElements(),
                sponsorshipsPage.getTotalPages(),
                sponsorshipsPage.isFirst(),
                sponsorshipsPage.isLast()
        );
    }
    @CacheEvict(value = "sponsorAds", allEntries = true)
    public void deleteSponsorship(Integer sponsorshipId, Authentication connectedUser) {
        Sponsorship sponsorship = sponsorshipRepository.findById(sponsorshipId)
                .orElseThrow(() -> new RuntimeException("Sponsorship not found"));

        if (sponsorship.getStatus() != SponsorshipStatus.PENDING &&
                sponsorship.getStatus() != SponsorshipStatus.REJECTED &&
                sponsorship.getStatus() != SponsorshipStatus.FINISHED) {
            throw new IllegalStateException("Only PENDING, REJECTED, or FINISHED sponsorships can be deleted.");
        }

        boolean isAdmin = connectedUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_Admin"));

        boolean isAdvertiser = connectedUser.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_Advertiser"));

        if (!isAdmin && (!isAdvertiser || !sponsorship.getUserId().equals(connectedUser.getName()))) {
            throw new ForbiddenActionException("You are not authorized to perform this action.");
        }

        // Detach sponsorAds before deleting them
        Set<SponsorAd> relatedAds = new HashSet<>(sponsorship.getSponsorAds());

        for (SponsorAd ad : relatedAds) {
            ad.getSponsorships().remove(sponsorship);           // Remove sponsorship from ad
        }
        sponsorship.getSponsorAds().clear();                    // Remove ads from sponsorship

        sponsorshipRepository.save(sponsorship); // Persist the relationship break before deletion

        // Now safely delete the ads if they are orphan
        for (SponsorAd ad : relatedAds) {
            if (ad.getSponsorships().isEmpty()) {
                cloudinaryService.deleteImage(ad.getDesign());
                sponsorAdRepository.delete(ad);
            }
        }

        // Finally, delete the sponsorship
        sponsorshipRepository.delete(sponsorship);
    }

/*    public void deleteSponsorship(Integer sponsorshipId, Authentication connectedUser) {
        // Find the sponsorship by ID
        Sponsorship sponsorship = sponsorshipRepository.findById(sponsorshipId)
                .orElseThrow(() -> new RuntimeException("Sponsorship not found"));

        // Check if the sponsorship status allows deletion
        if (sponsorship.getStatus() != SponsorshipStatus.PENDING &&
                sponsorship.getStatus() != SponsorshipStatus.REJECTED &&
                sponsorship.getStatus() != SponsorshipStatus.FINISHED) {
            throw new IllegalStateException("Only PENDING, REJECTED, or FINISHED sponsorships can be deleted.");
        }

        // Check if the user is an Admin or the creator of the sponsorship (Advertiser)
        boolean isAdmin = connectedUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_Admin"));

        boolean isAdvertiser = connectedUser.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_Advertiser"));

        if (isAdmin) {
            sponsorshipRepository.delete(sponsorship);
        } else if (isAdvertiser && sponsorship.getUserId().equals(connectedUser.getName())) {
            if (sponsorship.getStatus() == SponsorshipStatus.PENDING ||
                    sponsorship.getStatus() == SponsorshipStatus.REJECTED) {
                sponsorshipRepository.delete(sponsorship);
            } else {
                throw new UnauthorizedAccessException("You are not authorized to delete this sponsorship.");
            }
        } else {
            throw new ForbiddenActionException("You are not authorized to perform this action.");
        }
    }*/


    public SponsorshipResponse updateSponsorshipStatus(Authentication connectedUser, Integer sponsorshipId, SponsorshipStatus newStatus) {
        // Retrieve the Sponsorship
        Sponsorship sponsorship = sponsorshipRepository.findById(sponsorshipId)
                .orElseThrow(() -> new EntityNotFoundException("Sponsorship not found"));



        // Check if the status transition is allowed
        SponsorshipStatus currentStatus = sponsorship.getStatus();
        if (currentStatus == SponsorshipStatus.FINISHED) {
            throw new BusinessException(BusinessErrorCodes.INVALID_SPONSORSHIP_STATUS, "Sponsorship must be in PENDING state to proceed.");
        }

        if (currentStatus == SponsorshipStatus.APPROVED && newStatus == SponsorshipStatus.REJECTED) {
            throw new OperationNotPermittedException("Cannot reject an already approved Sponsorship.");
        }

        // Update status
        sponsorship.setStatus(newStatus);
        sponsorshipRepository.save(sponsorship);

        return SponsorshipMapper.toSponsorshipResponse(sponsorship);
    }


    @CacheEvict(value = "sponsorAds", allEntries = true)
    public SponsorshipResponse patchSponsorship(Integer sponsorshipId, SponsorshipPatchRequest request,
                                                MultipartFile image, Authentication connectedUser) {

        Sponsorship sponsorship = sponsorshipRepository.findById(sponsorshipId)
                .orElseThrow(() -> new EntityNotFoundException("Sponsorship not found"));
        boolean isAdmin = connectedUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_Admin"));

        if (!isAdmin) {
            throw new AccessDeniedException("Only Admins can patch sponsorships");
        }

        // Only update the status if it was provided in the request
        if (request != null && request.getNewStatus() != null) {
            sponsorship.setStatus(request.getNewStatus());
        }

        // Only update SponsorAd fields if request is not null and contains data, or if an image is provided
        if (request != null && (request.getSponsorAdData() != null || (image != null && !image.isEmpty()))) {
            for (SponsorAd ad : sponsorship.getSponsorAds()) {
                sponsorAdMapper.updateSponsorAdFromRequest(request.getSponsorAdData(), ad, image);
                sponsorAdRepository.save(ad);
            }
        }

        sponsorshipRepository.save(sponsorship);
        return SponsorshipMapper.toSponsorshipResponse(sponsorship);
    }

}

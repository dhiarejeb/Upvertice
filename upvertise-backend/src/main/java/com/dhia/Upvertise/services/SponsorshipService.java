package com.dhia.Upvertise.services;

import com.dhia.Upvertise.dto.SponsorshipResponse;
import com.dhia.Upvertise.handler.*;
import com.dhia.Upvertise.mapper.SponsorshipMapper;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.models.sponsorship.Sponsorship;
import com.dhia.Upvertise.models.sponsorship.SponsorshipStatus;
import com.dhia.Upvertise.repositories.SponsorshipRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class SponsorshipService {

    private final SponsorshipRepository sponsorshipRepository;


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


    public void deleteSponsorship(Integer sponsorshipId, Authentication connectedUser) {
        // Find the sponsorship by ID
        Sponsorship sponsorship = sponsorshipRepository.findById(sponsorshipId)
                .orElseThrow(() -> new RuntimeException("Sponsorship not found"));

        // Check if the sponsorship status allows deletion
        if (sponsorship.getStatus() != SponsorshipStatus.PENDING &&
                sponsorship.getStatus() != SponsorshipStatus.REJECTED&&
                sponsorship.getStatus() != SponsorshipStatus.FINISHED) {
            throw new IllegalStateException("Only PENDING or REJECTED or FINISHED sponsorships can be deleted.");
        }
        // Ensure only the sponsor who created it or an admin can delete
        boolean isAdmin = connectedUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_Admin"));
        boolean isAdvertiser = connectedUser.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_Advertiser"));


        if (isAdmin) {
            sponsorshipRepository.delete(sponsorship);

        }else{
            throw new ForbiddenActionException("Only Admins can delete this sponsorship.");

        }
        // Sponsor can delete only if status is PENDING or REJECTED
        if (isAdvertiser && sponsorship.getUserId().equals(connectedUser.getName())) {
            if (sponsorship.getStatus() == SponsorshipStatus.PENDING ||
                    sponsorship.getStatus() == SponsorshipStatus.REJECTED) {
                sponsorshipRepository.delete(sponsorship);

            } else {
                throw new UnauthorizedAccessException("You are not authorized to delete this sponsorship");
            }
        }

        // Perform the deletion (SponsorOffers remain untouched)

    }


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

}

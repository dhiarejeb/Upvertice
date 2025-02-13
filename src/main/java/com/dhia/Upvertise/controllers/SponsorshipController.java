package com.dhia.Upvertise.controllers;

import com.dhia.Upvertise.dto.SponsorshipResponse;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.models.sponsorship.Sponsorship;
import com.dhia.Upvertise.models.sponsorship.SponsorshipStatus;
import com.dhia.Upvertise.repositories.SponsorshipRepository;
import com.dhia.Upvertise.services.SponsorshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sponsorships")
@RequiredArgsConstructor
public class SponsorshipController {

    private final SponsorshipService sponsorshipService;

    @GetMapping
    @PreAuthorize("hasAnyRole('Admin','Sponsor')")
    public ResponseEntity<PageResponse<SponsorshipResponse>> getAllSponsorships(
            Authentication connectedUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(sponsorshipService.getAllSponsorships(connectedUser, page, size));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('Admin','Sponsor')")
    public ResponseEntity<PageResponse<SponsorshipResponse>> getSponsorshipsByStatus(
            @PathVariable SponsorshipStatus status,
            @RequestParam(defaultValue = "0") int page,  // Default page is 0
            @RequestParam(defaultValue = "10") int size, // Default size is 10
            Authentication connectedUser
    ) {
        PageResponse<SponsorshipResponse> sponsorships = sponsorshipService.getSponsorshipsByStatus(status, connectedUser, page, size);
        return ResponseEntity.ok(sponsorships);
    }


    @DeleteMapping("/{sponsorshipId}/delete")
    @PreAuthorize("hasAnyRole('Admin','Sponsor')")
    public ResponseEntity<?> deleteSponsorship(
            @PathVariable Integer sponsorshipId,
            Authentication connectedUser) {
        sponsorshipService.deleteSponsorship(sponsorshipId, connectedUser);
        return ResponseEntity.ok("Sponsorship deleted successfully");
    }

    @PatchMapping("/{sponsorshipId}/status")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> updateSponsorshipStatus(
            @PathVariable Integer sponsorshipId,
            @RequestParam SponsorshipStatus newStatus,
            Authentication connectedUser) {

        SponsorshipResponse updatedSponsorship = sponsorshipService.updateSponsorshipStatus(connectedUser, sponsorshipId, newStatus);
        return ResponseEntity.ok(updatedSponsorship);
    }
}


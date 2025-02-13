package com.dhia.Upvertise.controllers;

import com.dhia.Upvertise.dto.SponsorAdRequest;
import com.dhia.Upvertise.dto.SponsorOfferRequest;
import com.dhia.Upvertise.dto.SponsorOfferResponse;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.models.sponsorship.SponsorOfferStatus;
import com.dhia.Upvertise.services.SponsorOfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sponsor-offers")
@RequiredArgsConstructor
public class SponsorOfferController {

    private final SponsorOfferService sponsorOfferService;



    @GetMapping("/sponsorOffersByStatus")
    @PreAuthorize("hasAnyRole('Admin','Sponsor')")
    public PageResponse<SponsorOfferResponse> getSponsorOffersByStatus(
            @RequestParam String status ,
            Pageable pageable) {
        // Convert the String to Enum
        SponsorOfferStatus sponsorOfferStatus;
        try {
            sponsorOfferStatus = SponsorOfferStatus.valueOf(status.toUpperCase()); // Ensure case-insensitive match
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value: " + status); // Handle invalid enum values
        }
        return sponsorOfferService.getSponsorOffersByStatus(pageable,sponsorOfferStatus);
    }
    @GetMapping("/allSponsorOffers")
    @PreAuthorize("hasAnyRole('Admin','Sponsor')")
    public ResponseEntity<PageResponse<SponsorOfferResponse>> getAllSponsorOffers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(sponsorOfferService.getAllSponsorOffers(page, size));
    }
    @PostMapping("/createSponsorOffer")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<String> createSponsorOffer(
            @RequestBody SponsorOfferRequest sponsorOfferRequest,
            Authentication connectedUser) {

        Integer offerId = sponsorOfferService.createSponsorOffer(sponsorOfferRequest, connectedUser);
        return ResponseEntity.ok("Sponsor Offer created successfully. Offer ID: " + offerId);
    }

    // sponsor selects a specific sponsor offer
    @PostMapping("/chooseSponsorOffer/{offerId}")
    @PreAuthorize("hasRole('Sponsor')")
    public ResponseEntity<Integer> chooseSponsorOffer(
            @PathVariable Integer offerId,
            @RequestBody SponsorAdRequest sponsorAdRequest,
            Authentication connectedUser) {

        return ResponseEntity.ok(sponsorOfferService.chooseSponsorOffer(offerId, sponsorAdRequest , connectedUser));
    }
    @PutMapping("/updateSponsorOfferChoice")
    @PreAuthorize("hasRole('Sponsor')")
    public ResponseEntity<String> updateChosenSponsorOffer(
            @RequestParam Integer oldOfferId,
            @RequestParam Integer newOfferId,
            Authentication connectedUser) {

        Integer updatedSponsorshipId = sponsorOfferService.updateChosenSponsorOffer(oldOfferId, newOfferId, connectedUser);

        return ResponseEntity.ok("Sponsorship updated successfully. Sponsorship ID: " + updatedSponsorshipId
                + " | Old Offer ID: " + oldOfferId + " | New Offer ID: " + newOfferId);
    }

    @PutMapping("/{offerId}/updateSponsorOffer")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<SponsorOfferResponse> updateSponsorOffer(
            @PathVariable Integer offerId,
            @RequestBody SponsorOfferRequest request,
            Authentication connectedUser) {

        SponsorOfferResponse response = sponsorOfferService.updateSponsorOffer(connectedUser, offerId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{offerId}/deleteSponsorOffer")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Void> deleteSponsorOffer(
            @PathVariable Integer offerId,
            Authentication connectedUser) {
        sponsorOfferService.deleteSponsorOffer(connectedUser, offerId);
        return ResponseEntity.noContent().build();
    }

}

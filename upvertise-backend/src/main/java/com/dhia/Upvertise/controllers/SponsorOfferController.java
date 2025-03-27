package com.dhia.Upvertise.controllers;

import com.dhia.Upvertise.dto.SponsorAdRequest;
import com.dhia.Upvertise.dto.SponsorOfferRequest;
import com.dhia.Upvertise.dto.SponsorOfferResponse;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.models.sponsorship.SponsorOfferStatus;
import com.dhia.Upvertise.services.SponsorOfferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/sponsor-offers")
@RequiredArgsConstructor
public class SponsorOfferController {

    private final SponsorOfferService sponsorOfferService;



    @GetMapping("/sponsorOffersByStatus")
    @PreAuthorize("hasAnyRole('Admin','Advertiser')")
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
    @PreAuthorize("hasAnyRole('Admin','Advertiser')")
    public ResponseEntity<PageResponse<SponsorOfferResponse>> getAllSponsorOffers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(sponsorOfferService.getAllSponsorOffers(page, size));
    }
    @PostMapping(value = "/createSponsorOffer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<String> createSponsorOffer(
            @RequestPart(value = "request", required = true) String sponsorOfferJson,
            @RequestPart(value = "explainImages", required = false) List<MultipartFile> images,
            Authentication connectedUser) {
        try {
            // Convert JSON String to SponsorOfferRequest object
            ObjectMapper objectMapper = new ObjectMapper();
            SponsorOfferRequest sponsorOfferRequest = objectMapper.readValue(sponsorOfferJson, SponsorOfferRequest.class);

            Integer offerId = sponsorOfferService.createSponsorOffer(sponsorOfferRequest, images, connectedUser);
            return ResponseEntity.ok("Sponsor Offer created successfully. Offer ID: " + offerId);
        } catch (Exception e) {
            e.printStackTrace();  // Log the full error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    /*@PostMapping(value = "/createSponsorOffer" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<String> createSponsorOffer(
            @RequestPart("request") SponsorOfferRequest sponsorOfferRequest, // JSON request body
            @RequestPart(value = "explainImages" , required = false) List<MultipartFile> images,
            Authentication connectedUser) {

        Integer offerId = sponsorOfferService.createSponsorOffer(sponsorOfferRequest,images,connectedUser);
        return ResponseEntity.ok("Sponsor Offer created successfully. Offer ID: " + offerId);
    }*/

    // sponsor selects a specific sponsor offer
    @PostMapping("/chooseSponsorOffer/{offerId}")
    @PreAuthorize("hasRole('Advertiser')")
    public ResponseEntity<Integer> chooseSponsorOffer(
            @PathVariable Integer offerId,
            @RequestPart("request") SponsorAdRequest sponsorAdRequest,
            @RequestPart("images")MultipartFile image,
            Authentication connectedUser) {

        return ResponseEntity.ok(sponsorOfferService.chooseSponsorOffer(offerId,image, sponsorAdRequest , connectedUser));
    }
    @PutMapping("/updateSponsorOfferChoice")
    @PreAuthorize("hasRole('Advertiser')")
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
            @RequestPart("offerId") Integer offerId,
            @RequestPart("sponsorOfferRequest") SponsorOfferRequest request ,
            @RequestPart("explainImages") List<MultipartFile> images
            ) {

        SponsorOfferResponse response = sponsorOfferService.updateSponsorOffer(offerId, request, images);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{offerId}/deleteSponsorOffer")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Void> deleteSponsorOffer(
            @PathVariable Integer offerId ) {
        sponsorOfferService.deleteSponsorOffer(offerId);
        return ResponseEntity.noContent().build();
    }

}

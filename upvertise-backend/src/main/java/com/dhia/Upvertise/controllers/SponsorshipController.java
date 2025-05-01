package com.dhia.Upvertise.controllers;

import com.dhia.Upvertise.dto.SponsorshipPatchMultipartRequest;
import com.dhia.Upvertise.dto.SponsorshipPatchRequest;
import com.dhia.Upvertise.dto.SponsorshipResponse;

import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.models.sponsorship.Sponsorship;
import com.dhia.Upvertise.models.sponsorship.SponsorshipStatus;
import com.dhia.Upvertise.repositories.SponsorshipRepository;
import com.dhia.Upvertise.services.SponsorshipService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/sponsorships")
@RequiredArgsConstructor
public class SponsorshipController {

    private final SponsorshipService sponsorshipService;
    private final ObjectMapper objectMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('Admin','Advertiser')")
    public ResponseEntity<PageResponse<SponsorshipResponse>> getAllSponsorships(
            Authentication connectedUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(sponsorshipService.getAllSponsorships(connectedUser, page, size));
    }

    @GetMapping("/{sponsorshipId}")
    @PreAuthorize("hasAnyRole('Admin','Advertiser')")
    public ResponseEntity<SponsorshipResponse> getSponsorshipById(
            Authentication connectedUser,
            @PathVariable Integer sponsorshipId) {
        return ResponseEntity.ok(sponsorshipService.getSponsorshipById(connectedUser, sponsorshipId));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('Admin','Advertiser')")
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
    @PreAuthorize("hasAnyRole('Admin','Advertiser')")
    public ResponseEntity<?> deleteSponsorship(
            @PathVariable Integer sponsorshipId,
            Authentication connectedUser) {

        sponsorshipService.deleteSponsorship(sponsorshipId, connectedUser);
        //return ResponseEntity.ok("Sponsorship deleted successfully");
        return ResponseEntity.ok().build(); // ✅ empty body, HTTP 200
    }

    @Operation(
            summary = "Patch Sponsorship Status and SponsorAd",
            description = "Allows admin to patch the status of a sponsorship and optionally upload an image and sponsorAd data",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = SponsorshipPatchMultipartRequest.class)
                    )
            )
    )
    @PatchMapping(value = "/{sponsorshipId}/status", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<SponsorshipResponse> patchSponsorship(
            @PathVariable Integer sponsorshipId,
            @RequestPart("request") String requestJson,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication connectedUser) throws JsonProcessingException {

        SponsorshipPatchRequest request = null;
        if (!requestJson.isEmpty()) {
            request = objectMapper.readValue(requestJson, SponsorshipPatchRequest.class);
        }
        SponsorshipResponse updated = sponsorshipService.patchSponsorship(sponsorshipId, request, image, connectedUser);
        return ResponseEntity.ok(updated);
    }
    /*@PatchMapping("/{sponsorshipId}/status")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> updateSponsorshipStatus(
            @PathVariable Integer sponsorshipId,
            @RequestParam SponsorshipStatus newStatus,
            Authentication connectedUser) {

        SponsorshipResponse updatedSponsorship = sponsorshipService.updateSponsorshipStatus(connectedUser, sponsorshipId, newStatus);
        return ResponseEntity.ok(updatedSponsorship);
    }*/



}


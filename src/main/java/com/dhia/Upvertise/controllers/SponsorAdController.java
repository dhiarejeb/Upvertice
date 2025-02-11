package com.dhia.Upvertise.controllers;

import com.dhia.Upvertise.dto.SponsorAdRequest;
import com.dhia.Upvertise.dto.SponsorAdResponse;
import com.dhia.Upvertise.services.SponsorAdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sponsorAd")
@RequiredArgsConstructor

public class SponsorAdController {

    private final SponsorAdService sponsorAdService ;


    @PutMapping("/updateAd/{adId}")
    @PreAuthorize("hasAnyRole('Admin','Sponsor')")
    public ResponseEntity<SponsorAdResponse> updateSponsorAd(
            Authentication connectedUser,
            @PathVariable Integer adId,
            @RequestBody SponsorAdRequest request) {

        return ResponseEntity.ok(sponsorAdService.updateSponsorAd(connectedUser, adId, request));
    }
}

package com.dhia.Upvertise.controllers;

import com.dhia.Upvertise.dto.SponsorAdMultipartRequest;
import com.dhia.Upvertise.dto.SponsorAdRequest;
import com.dhia.Upvertise.dto.SponsorAdResponse;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.services.SponsorAdService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/sponsorAd")
@RequiredArgsConstructor

public class SponsorAdController {

    private final SponsorAdService sponsorAdService ;
    private final ObjectMapper objectMapper;

    @GetMapping("/sponsorAds")
    @PreAuthorize("hasAnyRole('Admin', 'Advertiser')")
    public ResponseEntity<PageResponse<SponsorAdResponse>> getAllSponsorAds(
            Authentication connectedUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<SponsorAdResponse> sponsorAdResponses = sponsorAdService.getAllSponsorAds(connectedUser, page, size);
        return ResponseEntity.ok(sponsorAdResponses);
    }

    @DeleteMapping("/{adId}/deleteAd")
    @PreAuthorize("hasAnyRole('Admin','Advertiser')")
    public ResponseEntity<?> deleteSponsorAd(
            @PathVariable Integer adId,
            Authentication connectedUser) {
        sponsorAdService.deleteSponsorAd(connectedUser, adId);
        return ResponseEntity.ok("Sponsor Ad deleted successfully");
    }


    /*@PutMapping("/updateAd/{adId}")
    @PreAuthorize("hasAnyRole('Admin','Advertiser')")
    public ResponseEntity<SponsorAdResponse> updateSponsorAd(
            Authentication connectedUser,
            @PathVariable Integer adId,
            @RequestPart("request") @Valid SponsorAdRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        return ResponseEntity.ok(sponsorAdService.updateSponsorAd(connectedUser, adId, request,image));
    }*/
    @Operation(
            summary = "Update Sponsor Ad",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = SponsorAdMultipartRequest.class)
                    )
            )
    )
    @PutMapping(value = "/updateAd/{adId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('Admin','Advertiser')")
    public ResponseEntity<SponsorAdResponse> updateSponsorAd(
            @PathVariable Integer adId,
            @RequestPart("request") String sponsorAdJson,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @Parameter(hidden = true) Authentication connectedUser
    ) throws JsonProcessingException {

        SponsorAdRequest request = objectMapper.readValue(sponsorAdJson, SponsorAdRequest.class);
        SponsorAdResponse updatedAd = sponsorAdService.updateSponsorAd(connectedUser, adId, request, image);
        return ResponseEntity.ok(updatedAd);
    }
}

package com.dhia.Upvertise.controllers;

import com.dhia.Upvertise.dto.*;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.models.sponsorship.SponsorOfferStatus;
import com.dhia.Upvertise.services.SponsorOfferService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.StringToClassMapItem;

import java.util.List;

@RestController
@RequestMapping("/sponsor-offers")
@RequiredArgsConstructor
public class SponsorOfferController {

    private final SponsorOfferService sponsorOfferService;
    private final ObjectMapper objectMapper;



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

    @Operation(
            summary = "Create Sponsor Offer",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = SponsorOfferMultipartRequest.class)
                    )
            )
    )
    @PostMapping(value = "/createSponsorOffer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<SponsorOfferResponse> createSponsorOffer(
            @RequestPart(value = "request") String sponsorOfferJson,
            @RequestPart(value = "explainImages", required = false) List<MultipartFile> images,
            Authentication connectedUser) {

        try {
            SponsorOfferRequest sponsorOfferRequest = objectMapper.readValue(sponsorOfferJson, SponsorOfferRequest.class);
            SponsorOfferResponse createdOffer = sponsorOfferService.createSponsorOffer(sponsorOfferRequest, images, connectedUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOffer);
        } catch (Exception e) {
            e.printStackTrace(); // or use logger
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        }
    }


    // sponsor selects a specific sponsor offer
    @Operation(
            summary = "choose Sponsor Offer",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = SponsorOfferMultipartChooseRequest.class)
                    )
            )
    )
    @PostMapping("/chooseSponsorOffer/{offerId}")
    @PreAuthorize("hasRole('Advertiser')")
    public ResponseEntity<Integer> chooseSponsorOffer(
            @PathVariable Integer offerId,
            @RequestPart("request") String sponsorAdRequestJson,
            @RequestPart(value = "images", required = false) MultipartFile image,
            Authentication connectedUser) throws JsonProcessingException {

        SponsorAdRequest sponsorAdRequest = objectMapper.readValue(sponsorAdRequestJson, SponsorAdRequest.class);

        return ResponseEntity.ok(sponsorOfferService.chooseSponsorOffer(offerId, image, sponsorAdRequest, connectedUser));
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

    /*@PatchMapping("/{offerId}/updateSponsorOffer")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<SponsorOfferResponse> updateSponsorOffer(
            @PathVariable("offerId") Integer offerId,
            @RequestPart("SponsorOfferRequest") SponsorOfferRequest request ,
            @RequestPart("explainImages") List<MultipartFile> images
            ) {

        SponsorOfferResponse response = sponsorOfferService.updateSponsorOffer(offerId, request, images);
        return ResponseEntity.ok(response);
    }*/
    @Operation(
            summary = "Update Sponsor Offer",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = SponsorOfferMultipartRequest.class)
                    )
            )
    )
    @PatchMapping(value = "/{offerId}/updateSponsorOffer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<SponsorOfferResponse> updateSponsorOffer(
            @PathVariable("offerId") Integer offerId,
            @RequestPart("request") String sponsorOfferRequestStr,
            @RequestPart(value = "explainImages", required = false) List<MultipartFile> images
    ) throws JsonProcessingException {

        SponsorOfferRequest sponsorOfferRequest = objectMapper.readValue(sponsorOfferRequestStr, SponsorOfferRequest.class);
        SponsorOfferResponse response = sponsorOfferService.updateSponsorOffer(offerId, sponsorOfferRequest, images);
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

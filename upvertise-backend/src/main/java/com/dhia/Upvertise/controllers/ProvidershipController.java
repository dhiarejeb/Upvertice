package com.dhia.Upvertise.controllers;

import com.dhia.Upvertise.dto.ProvidershipMultipartRequest;
import com.dhia.Upvertise.dto.ProvidershipRequest;
import com.dhia.Upvertise.dto.ProvidershipResponse;
import com.dhia.Upvertise.dto.SponsorshipResponse;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.services.ProvidershipService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/providership")
@RequiredArgsConstructor
public class ProvidershipController {

    private final ProvidershipService providershipService;

    @GetMapping
    @PreAuthorize("hasAnyRole('Admin', 'Provider')")
    public ResponseEntity<PageResponse<ProvidershipResponse>> getAllProviderships(
            Authentication authentication, Pageable pageable) {

        PageResponse<ProvidershipResponse> response = providershipService.getProviderships(authentication, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{providershipId}")
    @PreAuthorize("hasAnyRole('Admin','Provider')")
    public ResponseEntity<ProvidershipResponse> getProvidershipById(
            Authentication connectedUser,
            @PathVariable Integer providershipId) {
        return ResponseEntity.ok(providershipService.getProvidershipById(connectedUser, providershipId));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('Admin', 'Provider')")
    public ResponseEntity<String> deleteProvidership(
            @PathVariable Integer id,
            Authentication authentication) {
        providershipService.deleteProvidership(id, authentication);
        return ResponseEntity.ok("Providership deleted successfully.");
    }


    @Operation(
            summary = "Update an existing Providership",
            description = "Allows a provider or admin to update providership info and upload new proof images",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = ProvidershipMultipartRequest.class)
                    )
            )
    )
    @PatchMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('Admin', 'Provider')")
    public ResponseEntity<ProvidershipResponse> updateProvidership(
            @PathVariable Integer id,
            @RequestPart("request") String requestJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> proofFiles,
            Authentication connectedUser) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ProvidershipRequest request = objectMapper.readValue(requestJson, ProvidershipRequest.class);
        ProvidershipResponse response = providershipService.updateProvidership(id, request, proofFiles, connectedUser);
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Create a new Providership",
            description = "Allows a provider to create a providership with optional proof images",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = ProvidershipMultipartRequest.class)
                    )
            )
    )
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('Provider')")
    public ResponseEntity<ProvidershipResponse> createProvidership(
            @RequestPart("request") String request,
            @RequestPart(value = "images", required = false) List<MultipartFile> proofFiles,
            Authentication connectedUser) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ProvidershipRequest providershipRequest = objectMapper.readValue(request, ProvidershipRequest.class);
        ProvidershipResponse response = providershipService.createProvidership(providershipRequest, proofFiles, connectedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


     /*@PatchMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('Admin', 'Provider')")
    public ResponseEntity<ProvidershipResponse> updateProvidership(
            @PathVariable Integer id,
            @RequestPart(value = "request", required = false) @Valid ProvidershipRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> proofFiles,
            Authentication connectedUser) {

        ProvidershipResponse response = providershipService.updateProvidership(id, request, proofFiles,connectedUser);
        return ResponseEntity.ok(response);
    }*/
    /* @PostMapping("/create")
    @PreAuthorize("hasRole('Provider')")
    public ResponseEntity<ProvidershipResponse> createProvidership(
            @RequestPart("request") ProvidershipRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> proofFiles,
            Authentication connectedUser) {
        ProvidershipResponse response = providershipService.createProvidership(request,proofFiles ,connectedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }*/

}

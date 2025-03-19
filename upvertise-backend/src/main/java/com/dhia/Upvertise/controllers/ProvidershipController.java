package com.dhia.Upvertise.controllers;

import com.dhia.Upvertise.dto.ProvidershipRequest;
import com.dhia.Upvertise.dto.ProvidershipResponse;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.services.ProvidershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('Admin', 'Provider')")
    public ResponseEntity<String> deleteProvidership(
            @PathVariable Integer id,
            Authentication authentication) {
        providershipService.deleteProvidership(id, authentication);
        return ResponseEntity.ok("Providership deleted successfully.");
    }

    @PatchMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('Admin', 'Provider')")
    public ResponseEntity<ProvidershipResponse> updateProvidership(
            @PathVariable Integer id,
            @RequestPart("request") @Valid ProvidershipRequest request,
            @RequestPart("images") List<MultipartFile> proofFiles,
            Authentication connectedUser) {

        ProvidershipResponse response = providershipService.updateProvidership(id, request, proofFiles,connectedUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('Provider')")
    public ResponseEntity<ProvidershipResponse> createProvidership(
            @RequestPart("request") ProvidershipRequest request,
            @RequestPart("images") List<MultipartFile> proofFiles,
            Authentication connectedUser) {
        ProvidershipResponse response = providershipService.createProvidership(request,proofFiles ,connectedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}

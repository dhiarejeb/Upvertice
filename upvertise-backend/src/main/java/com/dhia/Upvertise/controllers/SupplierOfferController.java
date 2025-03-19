package com.dhia.Upvertise.controllers;

import com.dhia.Upvertise.dto.SupplierOfferRequest;
import com.dhia.Upvertise.dto.SupplierOfferResponse;
import com.dhia.Upvertise.dto.SupplierTransactionResponse;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.models.supplier.SupplierOfferStatus;
import com.dhia.Upvertise.services.SupplierOfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/supplier-offers")
@RequiredArgsConstructor
public class SupplierOfferController {
    private final SupplierOfferService service;

    @GetMapping("AllSupplierOffers")
    //@PreAuthorize("hasAnyRole('Admin','Su')")
    public ResponseEntity<PageResponse<SupplierOfferResponse>> getAllSupplierOffers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.getAllSupplierOffers(pageable));
    }

    @GetMapping("/status")
    public ResponseEntity<PageResponse<SupplierOfferResponse>> getSupplierOffersByStatus(
            @RequestParam SupplierOfferStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.getSupplierOfferByStatus(status, pageable));
    }
    @PostMapping("/create")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<SupplierOfferResponse> createSupplierOffer(
            @RequestPart("request") SupplierOfferRequest request ,
            @RequestPart("image") MultipartFile image) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createSupplierOffer(request , image));
    }
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<SupplierOfferResponse> updateSupplierOffer(
            @PathVariable Integer id,
            @RequestPart("request") SupplierOfferRequest request,
            @RequestPart("newImage") MultipartFile image) {
        return ResponseEntity.ok(service.updateSupplierOffer(id, request , image));
    }
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Void> deleteSupplierOffer(
            @PathVariable Integer id) {
        service.deleteSupplierOffer(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{supplierOfferId}/choose")
    @PreAuthorize("hasRole('Supplier')")
    public ResponseEntity<SupplierTransactionResponse> chooseSupplierOffer(
            @PathVariable Integer supplierOfferId,
            Authentication connectedUser) {

        return ResponseEntity.ok(service.chooseSupplierOffer(connectedUser, supplierOfferId));
    }

    @PutMapping("/{transactionId}/update-choice/{newSupplierOfferId}")
    @PreAuthorize("hasRole('Supplier')")
    public ResponseEntity<SupplierTransactionResponse> updateSupplierChoice(
            @PathVariable Integer transactionId,
            @PathVariable Integer newSupplierOfferId,
            Authentication connectedUser) {

        SupplierTransactionResponse response = service.updateSupplierChoice(connectedUser, transactionId, newSupplierOfferId);
        return ResponseEntity.ok(response);
    }
}

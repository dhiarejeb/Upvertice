package com.dhia.Upvertise.controllers;

import com.dhia.Upvertise.dto.SupplierOfferMultipartRequest;
import com.dhia.Upvertise.dto.SupplierOfferRequest;
import com.dhia.Upvertise.dto.SupplierOfferResponse;
import com.dhia.Upvertise.dto.SupplierTransactionResponse;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.models.supplier.SupplierOfferStatus;
import com.dhia.Upvertise.services.SupplierOfferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
@RequestMapping("/supplier-offers")
@RequiredArgsConstructor
public class SupplierOfferController {
    private final SupplierOfferService service;

    @GetMapping("AllSupplierOffers")
    @PreAuthorize("hasAnyRole('Admin','Provider','Supplier')")
    public ResponseEntity<PageResponse<SupplierOfferResponse>> getAllSupplierOffers(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.getAllSupplierOffers(pageable, authentication));
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('Admin', 'Provider','Supplier')")
    public ResponseEntity<PageResponse<SupplierOfferResponse>> getSupplierOffersByStatus(
            @RequestParam SupplierOfferStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.getSupplierOfferByStatus(status, pageable));
    }
//    @PostMapping("/create")
//    @PreAuthorize("hasRole('Admin')")
//    public ResponseEntity<SupplierOfferResponse> createSupplierOffer(
//            @RequestPart("request") SupplierOfferRequest request ,
//            @RequestPart("image") MultipartFile image) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(service.createSupplierOffer(request , image));
//    }
@Operation(
        summary = "Create a new SupplierOffer",
        description = "Allows admin to create a supplier offer with optional image upload",
        requestBody = @RequestBody(
                content = @Content(
                        mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                        schema = @Schema(implementation = SupplierOfferMultipartRequest.class)
                )
        )
)
@PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
@PreAuthorize("hasAnyRole('Admin','Provider')")
public ResponseEntity<SupplierOfferResponse> createSupplierOffer(
        @RequestPart("request") String requestJson,
        @RequestPart(value = "image", required = false) MultipartFile image) {
    try {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        SupplierOfferRequest request = objectMapper.readValue(requestJson, SupplierOfferRequest.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createSupplierOffer(request, image));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
    @Operation(
            summary = "Update an existing SupplierOffer",
            description = "Allows admin or provider to update a supplier offer with optional new image",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = SupplierOfferMultipartRequest.class)
                    )
            )
    )
    @PatchMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('Admin', 'Provider')")
    public ResponseEntity<SupplierOfferResponse> updateSupplierOffer(
            @PathVariable Integer id,
            @RequestPart("request") String requestJson,
            @RequestPart(value = "newImage", required = false) MultipartFile image) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            SupplierOfferRequest request = objectMapper.readValue(requestJson, SupplierOfferRequest.class);
            return ResponseEntity.ok(service.updateSupplierOffer(id, request, image));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /*@PostMapping("/create")
    @PreAuthorize("hasAnyRole('Admin', 'Provider')")
    public ResponseEntity<SupplierOfferResponse> createSupplierOffer(
            @RequestPart("request") String requestJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        try {
            // Use ObjectMapper to deserialize the JSON string into SupplierOfferRequest
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules(); // This handles LocalDateTime and other Java 8 types

            SupplierOfferRequest request = objectMapper.readValue(requestJson, SupplierOfferRequest.class);

            SupplierOfferResponse response = service.createSupplierOffer(request, image);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }*/
//    @PutMapping("/update/{id}")
//    @PreAuthorize("hasAnyRole('Admin', 'Provider')")
//    public ResponseEntity<SupplierOfferResponse> updateSupplierOffer(
//            @PathVariable Integer id,
//            @RequestPart("request") SupplierOfferRequest request,
//            @RequestPart("newImage") MultipartFile image) {
//        return ResponseEntity.ok(service.updateSupplierOffer(id, request , image));
//    }
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('Admin', 'Provider')")
    public ResponseEntity<Void> deleteSupplierOffer(
            @PathVariable Integer id) {
        service.deleteSupplierOffer(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{supplierOfferId}/choose")
    @PreAuthorize("hasAnyRole('Supplier','Provider')")
    public ResponseEntity<List<SupplierTransactionResponse>> chooseSupplierOffer(
            @PathVariable Integer supplierOfferId,
            Authentication connectedUser) {

        List<SupplierTransactionResponse> txs = service.chooseSupplierOffer(connectedUser, supplierOfferId);
        return ResponseEntity.ok(txs);
    }
    /*@PostMapping("/{supplierOfferId}/choose")
    @PreAuthorize("hasAnyRole('Supplier', 'Provider')")
    public ResponseEntity<SupplierTransactionResponse> chooseSupplierOffer(
            @PathVariable Integer supplierOfferId,
            Authentication connectedUser) {

        return ResponseEntity.ok(service.chooseSupplierOffer(connectedUser, supplierOfferId));
    }*/

    @PutMapping("/{transactionId}/update-choice/{newSupplierOfferId}")
    @PreAuthorize("hasAnyRole('Supplier', 'Provider')")
    public ResponseEntity<SupplierTransactionResponse> updateSupplierChoice(
            @PathVariable Integer transactionId,
            @PathVariable Integer newSupplierOfferId,
            Authentication connectedUser) {

        SupplierTransactionResponse response = service.updateSupplierChoice(connectedUser, transactionId, newSupplierOfferId);
        return ResponseEntity.ok(response);
    }
}

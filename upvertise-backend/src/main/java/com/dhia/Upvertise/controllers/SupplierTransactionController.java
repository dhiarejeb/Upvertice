package com.dhia.Upvertise.controllers;


import com.dhia.Upvertise.dto.SupplierTransactionMultipartRequest;
import com.dhia.Upvertise.dto.SupplierTransactionResponse;
import com.dhia.Upvertise.dto.SupplierTransactionUpdateRequest;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.services.SupplierTransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import org.springframework.web.server.ResponseStatusException;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/supplier-transactions")
@RequiredArgsConstructor
public class SupplierTransactionController {
    private final SupplierTransactionService supplierTransactionService;

    // ✅ Get paginated SupplierTransactions for Admin or Supplier
    @GetMapping
    @PreAuthorize("hasAnyRole('Admin','Provider','Supplier')")
    public ResponseEntity<PageResponse<SupplierTransactionResponse>> getSupplierTransactions(
            Authentication connectedUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PageResponse<SupplierTransactionResponse> response = supplierTransactionService.getSupplierTransactions(connectedUser, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','Provider','Supplier')")
    public ResponseEntity<SupplierTransactionResponse> getTransactionById(
            @PathVariable Integer id,
            Authentication authentication) {
        SupplierTransactionResponse response = supplierTransactionService.getSupplierTransactionById(id, authentication);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update Supplier Transaction",
            description = "Update an existing Supplier Transaction with new data and images.",
            parameters = {
                    @Parameter(name = "transactionId", description = "ID of the Supplier Transaction to update", required = true)
            },
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = SupplierTransactionMultipartRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(description = "Updated Supplier Transaction", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SupplierTransactionResponse.class)))
            }
    )
    @PatchMapping("/update/{transactionId}")
    @PreAuthorize("hasAnyRole('Admin','Provider','Supplier')")
    public ResponseEntity<SupplierTransactionResponse> updateSupplierTransaction(
            @PathVariable Integer transactionId,
            @RequestPart(value = "request", required = false) String requestJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> proofsFiles,
            Authentication connectedUser) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            SupplierTransactionUpdateRequest request;

            if (requestJson != null) {
                request = objectMapper.readValue(requestJson, SupplierTransactionUpdateRequest.class);
            } else {
                // Create a default request with all fields null
                request = new SupplierTransactionUpdateRequest(null, null, null, null);
            }

            SupplierTransactionResponse updatedTransaction = supplierTransactionService
                    .updateSupplierTransaction(connectedUser, transactionId, request, proofsFiles);

            return ResponseEntity.ok(updatedTransaction);

        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON format", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", e);
        }
    }

    @DeleteMapping("delete/{transactionId}")
    @PreAuthorize("hasAnyRole('Admin','Provider','Supplier')")
    public ResponseEntity<Void> deleteSupplierTransaction(
            @PathVariable Integer transactionId,
            Authentication connectedUser) {

        supplierTransactionService.deleteSupplierTransaction(connectedUser, transactionId);
        return ResponseEntity.noContent().build();
    }
}

// ✅ Update SupplierTransaction (Admin can update status, Supplier can update quantitySold , proofs and locations if status is APPROVED)
    /*@PutMapping("/update/{transactionId}")
    @PreAuthorize("hasAnyRole('Admin','Supplier')")
    public ResponseEntity<SupplierTransactionResponse> updateSupplierTransaction(
            @PathVariable Integer transactionId,
            @RequestPart("request") SupplierTransactionUpdateRequest request,
            @RequestPart("images") List<MultipartFile> proofsFiles,
            Authentication connectedUser) {

        SupplierTransactionResponse updatedTransaction = supplierTransactionService.updateSupplierTransaction(
                connectedUser, transactionId, request, proofsFiles);

        return ResponseEntity.ok(updatedTransaction);
    }*/



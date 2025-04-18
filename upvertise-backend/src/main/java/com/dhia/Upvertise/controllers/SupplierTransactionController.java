package com.dhia.Upvertise.controllers;


import com.dhia.Upvertise.dto.SupplierTransactionResponse;
import com.dhia.Upvertise.dto.SupplierTransactionUpdateRequest;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.services.SupplierTransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/supplier-transactions")
@RequiredArgsConstructor
public class SupplierTransactionController {
    private final SupplierTransactionService supplierTransactionService;

    // ✅ Get paginated SupplierTransactions for Admin or Supplier
    @GetMapping
    @PreAuthorize("hasAnyRole('Admin','Supplier')")
    public ResponseEntity<PageResponse<SupplierTransactionResponse>> getSupplierTransactions(
            Authentication connectedUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PageResponse<SupplierTransactionResponse> response = supplierTransactionService.getSupplierTransactions(connectedUser, pageable);
        return ResponseEntity.ok(response);
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
    @PutMapping("/update/{transactionId}")
    @PreAuthorize("hasAnyRole('Admin','Provider','Supplier')")
    public ResponseEntity<SupplierTransactionResponse> updateSupplierTransaction(
            @PathVariable Integer transactionId,
            @RequestPart("request") String requestJson,
            @RequestPart("images") List<MultipartFile> proofsFiles,
            Authentication connectedUser) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            SupplierTransactionUpdateRequest request =
                    objectMapper.readValue(requestJson, SupplierTransactionUpdateRequest.class);

            SupplierTransactionResponse updatedTransaction = supplierTransactionService
                    .updateSupplierTransaction(connectedUser, transactionId, request, proofsFiles);

            return ResponseEntity.ok(updatedTransaction);

        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON in request part", e);
        }
    }

    @DeleteMapping("delete/{transactionId}")
    @PreAuthorize("hasAnyRole('Admin','Supplier')")
    public ResponseEntity<Void> deleteSupplierTransaction(
            @PathVariable Integer transactionId,
            Authentication connectedUser) {

        supplierTransactionService.deleteSupplierTransaction(connectedUser, transactionId);
        return ResponseEntity.noContent().build();
    }
}



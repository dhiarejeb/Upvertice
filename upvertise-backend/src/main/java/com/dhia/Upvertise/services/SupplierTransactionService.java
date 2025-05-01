package com.dhia.Upvertise.services;

import com.dhia.Upvertise.dto.SupplierTransactionResponse;
import com.dhia.Upvertise.dto.SupplierTransactionUpdateRequest;


import com.dhia.Upvertise.handler.OperationNotPermittedException;
import com.dhia.Upvertise.mapper.SupplierTransactionMapper;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.models.supplier.SupplierOffer;
import com.dhia.Upvertise.models.supplier.SupplierTransaction;
import com.dhia.Upvertise.models.supplier.SupplierTransactionStatus;
import com.dhia.Upvertise.repositories.SupplierOfferRepository;
import com.dhia.Upvertise.repositories.SupplierTransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierTransactionService {
    private final SupplierTransactionRepository transactionRepository;

    private final SupplierOfferRepository supplierOfferRepository;
    private final CloudinaryService cloudinaryService;
    //private final SupplierTransactionMapper supplierTransactionMapper;


    // Get SupplierTransactions (Admin can see all, Supplier can see only theirs)
    public PageResponse<SupplierTransactionResponse> getSupplierTransactions(Authentication connectedUser, Pageable pageable) {
        // Check if the user is Admin or Supplier
        Page<SupplierTransaction> transactionsPage;
        boolean isProvider = connectedUser.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_Provider"));
        boolean isSupplier = connectedUser.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_Supplier"));
        if (connectedUser.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_Admin"))) {
            // Admin: Get all SupplierTransactions
            transactionsPage = transactionRepository.findAll(pageable);
        }else if (isProvider) {
                transactionsPage = transactionRepository.findBySupplierOffer_CreatedBy(connectedUser.getName(), pageable);
            } else {
            // Supplier: Get only their SupplierTransactions
            transactionsPage = transactionRepository.findByUserId(connectedUser.getName(), pageable);
        }

        // Convert the Page into PageResponse
        List<SupplierTransactionResponse> content = transactionsPage.getContent()
                .stream()
                .map(SupplierTransactionMapper::toSupplierTransactionResponse)
                .collect(Collectors.toList());

        return PageResponse.<SupplierTransactionResponse>builder()
                .content(content)
                .number(transactionsPage.getNumber())
                .size(transactionsPage.getSize())
                .totalElements(transactionsPage.getTotalElements())
                .totalPages(transactionsPage.getTotalPages())
                .first(transactionsPage.isFirst())
                .last(transactionsPage.isLast())
                .build();
    }

    public SupplierTransactionResponse getSupplierTransactionById(Integer id, Authentication connectedUser) {
        SupplierTransaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SupplierTransaction not found with id: " + id));

        String currentUserId = connectedUser.getName();

        boolean isAdmin = connectedUser.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_Admin"));

        boolean isSupplier = connectedUser.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_Supplier"));

        boolean isProvider = connectedUser.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_Provider"));

        if (isAdmin
                || (isSupplier && transaction.getUserId().equals(currentUserId))
                || (isProvider && transaction.getSupplierOffer().getCreatedBy().equals(currentUserId))) {

            return SupplierTransactionMapper.toSupplierTransactionResponse(transaction);
        } else {
            throw new AccessDeniedException("You are not authorized to view this transaction");
        }
    }


    public SupplierTransactionResponse updateSupplierTransaction(
            Authentication connectedUser,
            Integer transactionId,
            SupplierTransactionUpdateRequest request,
            List<MultipartFile> proofsFiles) {

        // Fetch the SupplierTransaction
        SupplierTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new com.dhia.Upvertise.handler.EntityNotFoundException("SupplierTransaction", transactionId));

        // Determine user role
        boolean isAdmin = hasRole(connectedUser, "ROLE_Admin");
        boolean isProvider = hasRole(connectedUser, "ROLE_Provider");
        boolean isSupplier = hasRole(connectedUser, "ROLE_Supplier");

        if (isAdmin || isProvider) {
            updateTransactionStatus(transaction, request.status());
        }
        // Admin or Provider can set discount
        if (isAdmin || isProvider) {
            if (request.discount() != null) {
                transaction.setDiscount(request.discount());
            }
        }
        if (proofsFiles != null) { // Only validate if files are provided
            proofsFiles.forEach(file -> {
                if (file.isEmpty()) {
                    throw new OperationNotPermittedException("Empty file detected in uploaded images");
                }
            });
        }
        if (isSupplier) {
            if (transaction.getSupplierTransactionStatus() != SupplierTransactionStatus.APPROVED) {
                throw new OperationNotPermittedException("Suppliers can only update 'APPROVED' transactions.");
            }

            updateSupplierTransactionFields(transaction, request);
            // ✅ Handle proof image uploads to Cloudinary
            // ✅ Always store uploaded images into `proofs` field
            if (proofsFiles != null && !proofsFiles.isEmpty()) {
                List<String> uploadedProofs = proofsFiles.stream()
                        .map(cloudinaryService::uploadImage)
                        .collect(Collectors.toList());

                List<String> existingProofs = transaction.getProofs();
                existingProofs.addAll(uploadedProofs);
                transaction.setProofs(existingProofs);
            }

        }


        // Save updated transaction
        SupplierTransaction updatedTransaction = transactionRepository.save(transaction);

        // Return DTO response
        return SupplierTransactionMapper.toSupplierTransactionResponse(updatedTransaction);
    }
    private void updateTransactionStatus(SupplierTransaction transaction, SupplierTransactionStatus newStatus) {
        if (newStatus != null) {
            transaction.setSupplierTransactionStatus(newStatus);
        }
    }
    private void updateSupplierTransactionFields(SupplierTransaction transaction, SupplierTransactionUpdateRequest request) {
        SupplierOffer supplierOffer = supplierOfferRepository.findById(transaction.getSupplierOffer().getId())
                .orElseThrow(() -> new com.dhia.Upvertise.handler.EntityNotFoundException("SupplierOffer", transaction.getSupplierOffer().getId()));

        double price = supplierOffer.getPrice();
        int quantityAvailable = supplierOffer.getQuantityAvailable();

        if (request.quantitySold() != null && request.quantitySold() > 0) {
            int quantitySold = request.quantitySold();
            double relativePrice = calculateRelativePrice(quantitySold, price, quantityAvailable, transaction.getDiscount());
            double percentage = (double) quantitySold / quantityAvailable * 100;

            transaction.setQuantitySold(quantitySold);
            transaction.setRelativePrice(relativePrice);
            transaction.setPercentage(percentage);
        }
        //transaction.setDiscount(request.discount());

        // ⚠️ Only set locations if not null and not empty
        if (request.locations() != null && !request.locations().isEmpty()) {
            transaction.setLocations(request.locations());
        }
    }
    private double calculateRelativePrice(int quantitySold, double price, int quantityAvailable, Double discount) {
        double baseRelativePrice = (quantitySold * price) / quantityAvailable;

        if (discount != null && discount > 0) {
            return baseRelativePrice * (1 - (discount / 100));
        }

        return baseRelativePrice;
    }




    // ✅ Delete SupplierTransaction
    public void deleteSupplierTransaction(Authentication connectedUser, Integer transactionId) {
        SupplierTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("SupplierTransaction not found"));

        // Check if the user is an admin
        boolean isAdmin = connectedUser.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_Admin"));

        // Check if the user is the supplier who created the transaction
        boolean isSupplier = connectedUser.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_Supplier"));

        // Admin can delete any SupplierTransaction
        if (isAdmin) {
            deleteProofs(transaction.getProofs());
            transactionRepository.delete(transaction);
            return;
        }

        // Supplier can delete only if status is PENDING or REJECTED
        if (isSupplier && transaction.getUserId().equals(connectedUser.getName())) {
            if (transaction.getSupplierTransactionStatus() == SupplierTransactionStatus.PENDING ||
                    transaction.getSupplierTransactionStatus() == SupplierTransactionStatus.REJECTED) {
                deleteProofs(transaction.getProofs());
                transactionRepository.delete(transaction);
                return;
            } else {
                throw new IllegalStateException("You can only delete SupplierTransaction with status PENDING or REJECTED.");
            }
        }

        throw new AccessDeniedException("You are not authorized to delete this SupplierTransaction.");
    }



    
    // ✅ Uses CloudinaryService to delete images
    private void deleteProofs(List<String> proofs) {
        if (proofs == null || proofs.isEmpty()) return;
        proofs.forEach(cloudinaryService::deleteImage);
    }
    private boolean hasRole(Authentication user, String role) {
        return user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(role));
    }

}

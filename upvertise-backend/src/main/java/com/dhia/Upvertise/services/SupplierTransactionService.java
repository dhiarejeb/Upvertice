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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierTransactionService {
    private final SupplierTransactionRepository transactionRepository;

    private final SupplierOfferRepository supplierOfferRepository;
    private final CloudinaryService cloudinaryService;


    // Get SupplierTransactions (Admin can see all, Supplier can see only theirs)
    public PageResponse<SupplierTransactionResponse> getSupplierTransactions(Authentication connectedUser, Pageable pageable) {
        // Check if the user is Admin or Supplier
        Page<SupplierTransaction> transactionsPage;

        if (connectedUser.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_Admin"))) {
            // Admin: Get all SupplierTransactions
            transactionsPage = transactionRepository.findAll(pageable);
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
        boolean isSupplier = hasRole(connectedUser, "ROLE_Supplier");

        if (isAdmin) {
            updateTransactionStatus(transaction, request.status());
        }

        if (isSupplier) {
            if (transaction.getSupplierTransactionStatus() != SupplierTransactionStatus.APPROVED) {
                throw new OperationNotPermittedException("Suppliers can only update 'APPROVED' transactions.");
            }
            updateSupplierTransactionFields(transaction, request);
            // ✅ Handle proof image uploads to Cloudinary
            if (proofsFiles != null && !proofsFiles.isEmpty()) {
                List<String> uploadedProofs = proofsFiles.stream()
                        .map(cloudinaryService::uploadImage)
                        .collect(Collectors.toList());

                // Merge new proofs with existing ones
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

        if (request.locations() != null) {
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

    // ✅ Apply Discount to a SupplierTransaction
    public void applyDiscount(
            Integer supplierTransactionId ,
            Integer numberOfCompletedTransactionNeeded,
            Double discount) {
        SupplierTransaction transaction = transactionRepository.findById(supplierTransactionId)
                .orElseThrow(() -> new EntityNotFoundException("SupplierTransaction not found"));

        // Count completed transactions for the supplier
        Integer completedTransactions = transactionRepository.countByUserIdAndSupplierTransactionStatus(
                transaction.getUserId(), SupplierTransactionStatus.COMPLETED);

        // If supplier has completed at least 2 transactions, apply discount
        if (completedTransactions >= numberOfCompletedTransactionNeeded) {
            transaction.setDiscount(discount); // Example: 10% discount

            Double price = transaction.getSupplierOffer().getPrice();
            Double discountedPrice = price - (price * discount / 100);
            transaction.setRelativePrice(discountedPrice);
            transactionRepository.save(transaction);
        }

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

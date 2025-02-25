package com.dhia.Upvertise.services;

import com.dhia.Upvertise.dto.SupplierOfferRequest;
import com.dhia.Upvertise.dto.SupplierOfferResponse;
import com.dhia.Upvertise.dto.SupplierTransactionResponse;
import com.dhia.Upvertise.mapper.SupplierOfferMapper;
import com.dhia.Upvertise.mapper.SupplierTransactionMapper;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.models.sponsorship.SponsorAd;
import com.dhia.Upvertise.models.sponsorship.Sponsorship;
import com.dhia.Upvertise.models.supplier.SupplierOffer;
import com.dhia.Upvertise.models.supplier.SupplierOfferStatus;
import com.dhia.Upvertise.models.supplier.SupplierTransaction;
import com.dhia.Upvertise.models.supplier.SupplierTransactionStatus;
import com.dhia.Upvertise.repositories.SponsorAdRepository;
import com.dhia.Upvertise.repositories.SupplierOfferRepository;
import com.dhia.Upvertise.repositories.SupplierTransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierOfferService {
    private final SupplierOfferRepository supplierOfferRepository;
    private final SponsorAdRepository sponsorAdRepository;
    private final SupplierTransactionRepository suppliertransactionRepository;

    public PageResponse<SupplierOfferResponse> getAllSupplierOffers(Pageable pageable) {
        Page<SupplierOffer> page = supplierOfferRepository.findAll(pageable);
        List<SupplierOfferResponse> content = page.getContent().stream()
                .map(SupplierOfferMapper::toResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
    public PageResponse<SupplierOfferResponse> getSupplierOfferByStatus(SupplierOfferStatus status, Pageable pageable) {
        Page<SupplierOffer> page = supplierOfferRepository.findByStatus(status, pageable);
        List<SupplierOfferResponse> content = page.getContent().stream()
                .map(SupplierOfferMapper::toResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    public SupplierOfferResponse createSupplierOffer(SupplierOfferRequest request) {
        List<SponsorAd> sponsorAds = sponsorAdRepository.findAllById(request.sponsorAdIds());
        SupplierOffer supplierOffer = SupplierOfferMapper.toSupplierOffer(request,sponsorAds);
        SupplierOffer savedOffer = supplierOfferRepository.save(supplierOffer);
        return SupplierOfferMapper.toResponse(savedOffer);
    }

    public SupplierOfferResponse updateSupplierOffer(Integer id, SupplierOfferRequest request) {
        SupplierOffer existingOffer = supplierOfferRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SupplierOffer not found"));
        SupplierOfferMapper.updateSupplierOffer(existingOffer, request);
        SupplierOffer updatedOffer = supplierOfferRepository.save(existingOffer);
        return SupplierOfferMapper.toResponse(updatedOffer);
    }
    public void deleteSupplierOffer(Integer id) {
        SupplierOffer existingOffer = supplierOfferRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SupplierOffer not found"));
        supplierOfferRepository.delete(existingOffer);
    }

    public SupplierTransactionResponse chooseSupplierOffer(Authentication connectedUser, Integer supplierOfferId) {
        SupplierOffer supplierOffer = supplierOfferRepository.findById(supplierOfferId)
                .orElseThrow(() -> new EntityNotFoundException("SupplierOffer not found"));

        // Get all SponsorAds associated with the SupplierOffer
        List<SponsorAd> sponsorAds = supplierOffer.getSponsorAds();
        if (sponsorAds == null || sponsorAds.isEmpty()) {
            throw new EntityNotFoundException("No SponsorAds found for SupplierOffer");
        }

        // Initialize the common Sponsorship set with the Sponsorships from the first SponsorAd
        Iterator<SponsorAd> iterator = sponsorAds.iterator();
        Set<Sponsorship> commonSponsorships = new HashSet<>(iterator.next().getSponsorships());

        // Retain only the Sponsorships that are common across all SponsorAds
        for (SponsorAd ad : sponsorAds) {
            commonSponsorships.retainAll(ad.getSponsorships());
        }

        if (commonSponsorships.isEmpty()) {
            throw new IllegalStateException("No common Sponsorship found among SponsorAds");
        }
        Sponsorship sponsorship = commonSponsorships.iterator().next();

        SupplierTransaction transaction = new SupplierTransaction();
        transaction.setUserId(connectedUser.getName());
        transaction.setSupplierOffer(supplierOffer);
        transaction.setSupplierTransactionStatus(SupplierTransactionStatus.PENDING);
        transaction.setSponsorship(sponsorship);
        SupplierTransaction savedTransaction = suppliertransactionRepository.save(transaction);
        return SupplierTransactionMapper.toSupplierTransactionResponse(savedTransaction);

    }

    public SupplierTransactionResponse updateSupplierChoice(Authentication connectedUser, Integer transactionId, Integer newSupplierOfferId) {
        // Find the transaction and validate user
        SupplierTransaction transaction = suppliertransactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("SupplierTransaction not found"));

        if (!transaction.getUserId().equals(connectedUser.getName())) {
            throw new AccessDeniedException("You can only update your own transactions");
        }

        // Ensure the transaction is still PENDING
        if (!transaction.getSupplierTransactionStatus().equals(SupplierTransactionStatus.PENDING)) {
            throw new IllegalStateException("You can only update a PENDING transaction");
        }

        // Find the new SupplierOffer
        SupplierOffer newSupplierOffer = supplierOfferRepository.findById(newSupplierOfferId)
                .orElseThrow(() -> new EntityNotFoundException("New SupplierOffer not found"));

        // Update the transaction
        transaction.setSupplierOffer(newSupplierOffer);
        SupplierTransaction updatedTransaction = suppliertransactionRepository.save(transaction);

        // Convert to response DTO
        return SupplierTransactionMapper.toSupplierTransactionResponse(updatedTransaction);
    }

}

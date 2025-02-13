package com.dhia.Upvertise.services;

import com.dhia.Upvertise.dto.SponsorAdRequest;
import com.dhia.Upvertise.dto.SponsorOfferRequest;
import com.dhia.Upvertise.dto.SponsorOfferResponse;
import com.dhia.Upvertise.mapper.SponsorOfferMapper;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.models.sponsorship.*;
import com.dhia.Upvertise.repositories.SponsorAdRepository;
import com.dhia.Upvertise.repositories.SponsorOfferRepository;
import com.dhia.Upvertise.repositories.SponsorshipRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SponsorOfferService {
    private final SponsorOfferRepository sponsorOfferRepository;
    private final SponsorOfferMapper sponsorOfferMapper;
    private final SponsorshipRepository sponsorshipRepository;
    private final SponsorAdRepository sponsorAdRepository;


    public PageResponse<SponsorOfferResponse> getSponsorOffersByStatus(Pageable pageable, SponsorOfferStatus status) {
        Page<SponsorOffer> page = sponsorOfferRepository.findByStatus(status, pageable);
        return sponsorOfferMapper.toSponsorOfferPageResponse(page);
    }




    // Retrieve all sponsor offers with pagination
    public PageResponse<SponsorOfferResponse> getAllSponsorOffers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SponsorOffer> sponsorOffersPage = sponsorOfferRepository.findAll(pageable);

        List<SponsorOfferResponse> SponsorOfferResponses = sponsorOffersPage
                .getContent()
                .stream()
                .map(sponsorOfferMapper::toSponsorOfferResponse)
                .toList();
        return new PageResponse<>(
                SponsorOfferResponses,
                sponsorOffersPage.getNumber(),
                sponsorOffersPage.getSize(),
                sponsorOffersPage.getTotalElements(),
                sponsorOffersPage.getTotalPages(),
                sponsorOffersPage.isFirst(),
                sponsorOffersPage.isLast()
        );
    }

    // sponsor selects an offer
    // sponsor selects an offer
    public Integer chooseSponsorOffer(Integer offerId, SponsorAdRequest sponsorAdRequest, Authentication connectedUser) {

        // Retrieve the SponsorOffer
        SponsorOffer sponsorOffer = sponsorOfferRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Sponsor Offer not found"));

        // Create a new SponsorAd
        SponsorAd sponsorAd = new SponsorAd();
        sponsorAd.setDesign(sponsorAdRequest.design());
        sponsorAd.setContent(sponsorAdRequest.content());
        sponsorAd.setDesign_colors(sponsorAdRequest.designColors());
        sponsorAd.setUserId(connectedUser.getName());  // Link ad to sponsor

        // Save the SponsorAd first
        sponsorAdRepository.save(sponsorAd);

        // Create a new Sponsorship entry
        Sponsorship sponsorship = new Sponsorship();
        sponsorship.setUserId(connectedUser.getName());
        sponsorship.setSponsorOffer(sponsorOffer);
        sponsorship.setStatus(SponsorshipStatus.PENDING);

        // Save the Sponsorship
        Sponsorship savedSponsorship = sponsorshipRepository.save(sponsorship);

//        // Link the Sponsorship to the SponsorAd (many-to-many relationship)
//        sponsorAd.getSponsorships().add(savedSponsorship);
//
//        // Save the updated SponsorAd after linking the Sponsorship
//        sponsorAdRepository.save(sponsorAd);
        sponsorAd.getSponsorships().add(savedSponsorship);
        savedSponsorship.getSponsorAds().add(sponsorAd);

// Save the updated Sponsorship after linking the SponsorAd
        sponsorshipRepository.save(savedSponsorship);

        return savedSponsorship.getId();
    }
    public Integer updateChosenSponsorOffer(Integer oldOfferId, Integer newOfferId, Authentication connectedUser) {

        // Find the Sponsorship that has the oldOfferId
        Sponsorship sponsorship = sponsorshipRepository.findBySponsorOfferIdAndUserId(oldOfferId, connectedUser.getName())
                .orElseThrow(() -> new RuntimeException("No sponsorship found with the provided old offer ID for this user"));

        // Ensure the authenticated sponsor is the owner of the sponsorship
        if (!sponsorship.getCreatedBy().equals(connectedUser.getName())) {
            throw new RuntimeException("You are not authorized to update this sponsorship");
        }

        // Find the new SponsorOffer
        SponsorOffer newSponsorOffer = sponsorOfferRepository.findById(newOfferId)
                .orElseThrow(() -> new RuntimeException("New Sponsor Offer not found"));

        // Business Rule: Only allow updates if sponsorship is PENDING or REJECTED
        if (sponsorship.getStatus() != SponsorshipStatus.PENDING
                && sponsorship.getStatus() != SponsorshipStatus.REJECTED) {
            throw new IllegalStateException("You cannot update an offer for an approved or completed sponsorship");
        }

        // Update the sponsorship with the new sponsor offer
        sponsorship.setSponsorOffer(newSponsorOffer);

        // Save the updated sponsorship
        sponsorshipRepository.save(sponsorship);

        return sponsorship.getId();
    }

    public Integer createSponsorOffer(SponsorOfferRequest request, Authentication connectedUser) {
        // Get authenticated user details
        //UserDetails userDetails = (UserDetails) connectedUser.getPrincipal();


        // Create and save the SponsorOffer
        SponsorOffer sponsorOffer = new SponsorOffer();
        sponsorOffer.setTitle(request.title());
        sponsorOffer.setDescription(request.description());
        sponsorOffer.setPrice(request.price());
        sponsorOffer.setGobletQuantity(request.gobletQuantity());
        sponsorOffer.setExplainImage(request.explainImage());
        sponsorOffer.setStatus(request.status());
        sponsorOffer.setCategory(request.category());
        sponsorOffer.setUserId(connectedUser.getName());  // Link offer to admin who created it

        return sponsorOfferRepository.save(sponsorOffer).getId();
    }


    public SponsorOfferResponse updateSponsorOffer(Authentication connectedUser, Integer offerId, SponsorOfferRequest request) {



        // Retrieve the SponsorOffer
        SponsorOffer sponsorOffer = sponsorOfferRepository.findById(offerId)
                .orElseThrow(() -> new EntityNotFoundException("Sponsor Offer not found"));
        // Fetch associated Sponsorships, handling null or empty list
        List<Sponsorship> sponsorships = sponsorshipRepository.findBySponsorOffer(sponsorOffer);

        // Check if the list is null or empty (no active sponsorships)
        if (sponsorships == null || sponsorships.isEmpty()) {
            // No sponsorships, so update is allowed
            return updateSponsorOfferDetails(sponsorOffer, request);
        }else {

        // Check if any related Sponsorship has status other than FINISHED
        boolean canUpdate = sponsorships.stream()
                .anyMatch(sponsorship -> sponsorship.getStatus() == SponsorshipStatus.PENDING
                        || sponsorship.getStatus() == SponsorshipStatus.REJECTED
                        || sponsorship.getStatus() == SponsorshipStatus.FINISHED);

        if (!canUpdate) {
            throw new IllegalStateException("You cannot update an offer with an active or unfinished sponsorship");
        }

        // Apply changes and save the updated SponsorOffer
        return updateSponsorOfferDetails(sponsorOffer, request);
        }
    }

    private SponsorOfferResponse updateSponsorOfferDetails(SponsorOffer sponsorOffer, SponsorOfferRequest request) {
        // Apply changes from the request
        if (request.title() != null) sponsorOffer.setTitle(request.title());
        if (request.description() != null) sponsorOffer.setDescription(request.description());
        if (request.price() != null) sponsorOffer.setPrice(request.price());
        if (request.category() != null) sponsorOffer.setCategory(request.category());
        if (request.explainImage() != null) sponsorOffer.setExplainImage(request.explainImage());
        if (request.numberAds() != null) sponsorOffer.setNumberAds(request.numberAds());
        if (request.gobletQuantity() != null) sponsorOffer.setGobletQuantity(request.gobletQuantity());


        // Update SponsorOffer status if provided
        if (request.status() != null) sponsorOffer.setStatus(request.status());

        // Save the updated SponsorOffer
        sponsorOfferRepository.save(sponsorOffer);

        return sponsorOfferMapper.toSponsorOfferResponse(sponsorOffer);
    }


    public void deleteSponsorOffer(Authentication connectedUser, Integer offerId) {
        // Retrieve the SponsorOffer
        SponsorOffer sponsorOffer = sponsorOfferRepository.findById(offerId)
                .orElseThrow(() -> new EntityNotFoundException("Sponsor Offer not found"));

        // Fetch associated Sponsorships
        List<Sponsorship> sponsorships = sponsorshipRepository.findBySponsorOffer(sponsorOffer);

        // If no Sponsorships exist, delete immediately
        if (sponsorships == null || sponsorships.isEmpty()) {
            sponsorOfferRepository.delete(sponsorOffer);
            return;
        }

        // Check if all related Sponsorships have a valid status (PENDING, REJECTED, or FINISHED)
        boolean canDelete = sponsorships.stream()
                .allMatch(sponsorship -> sponsorship.getStatus() == SponsorshipStatus.PENDING
                        || sponsorship.getStatus() == SponsorshipStatus.REJECTED
                        || sponsorship.getStatus() == SponsorshipStatus.FINISHED);

        if (!canDelete) {
            throw new IllegalStateException("Cannot delete a Sponsor Offer with active or ongoing sponsorships");
        }

        // Update all related Sponsorships to FINISHED
        sponsorships.forEach(sponsorship -> sponsorship.setStatus(SponsorshipStatus.FINISHED));
        sponsorshipRepository.saveAll(sponsorships);

        // Delete the SponsorOffer
        sponsorOfferRepository.delete(sponsorOffer);

    }


}

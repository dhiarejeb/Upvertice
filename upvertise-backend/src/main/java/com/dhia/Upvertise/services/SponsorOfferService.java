package com.dhia.Upvertise.services;

import com.dhia.Upvertise.dto.SponsorAdRequest;
import com.dhia.Upvertise.dto.SponsorOfferRequest;
import com.dhia.Upvertise.dto.SponsorOfferResponse;
import com.dhia.Upvertise.handler.OperationNotPermittedException;
import com.dhia.Upvertise.mapper.SponsorOfferMapper;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.models.sponsorship.*;
import com.dhia.Upvertise.repositories.SponsorAdRepository;
import com.dhia.Upvertise.repositories.SponsorOfferRepository;
import com.dhia.Upvertise.repositories.SponsorshipRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SponsorOfferService {
    private final SponsorOfferRepository sponsorOfferRepository;
    private final SponsorshipRepository sponsorshipRepository;
    private final SponsorAdRepository sponsorAdRepository;
    private final CloudinaryService cloudinaryService;
    private final KafkaProducerService kafkaProducerService;
    private final SponsorOfferMapper sponsorOfferMapper;

    @Cacheable(value = "sponsorOffers", key = "'status:' + #status + '-' + #pageable.pageNumber")
    public PageResponse<SponsorOfferResponse> getSponsorOffersByStatus(Pageable pageable, SponsorOfferStatus status) {
        Page<SponsorOffer> page = sponsorOfferRepository.findByStatus(status, pageable);
        // Use the toResponseWithImagesUrls method here
        List<SponsorOfferResponse> sponsorOfferResponses = page
                .getContent()
                .stream()
                .map(sponsorOffer -> sponsorOfferMapper.toResponseWithImagesUrls(sponsorOffer))
                .collect(Collectors.toList());

        return new PageResponse<>(
                sponsorOfferResponses,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }




    // Retrieve all sponsor offers with pagination
    @Cacheable(value = "sponsorOffers", key = "'all:' + #page + '-' + #size")
    public PageResponse<SponsorOfferResponse> getAllSponsorOffers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SponsorOffer> sponsorOffersPage = sponsorOfferRepository.findAll(pageable);

        List<SponsorOfferResponse> sponsorOfferResponses = sponsorOffersPage
                .getContent()
                .stream()
                .map(sponsorOffer -> sponsorOfferMapper.toResponseWithImagesUrls(sponsorOffer))
                .collect(Collectors.toList());
        return new PageResponse<>(
                sponsorOfferResponses,
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
    @CacheEvict(value = "sponsorAds", allEntries = true)  // Clear cache after creating a new ad
    public Integer chooseSponsorOffer(Integer offerId,MultipartFile image, SponsorAdRequest sponsorAdRequest, Authentication connectedUser) {

        // Retrieve the SponsorOffer
        SponsorOffer sponsorOffer = sponsorOfferRepository.findById(offerId)
                .orElseThrow(() -> new EntityNotFoundException("Sponsor Offer not found"));

        // Upload the image to Cloudinary (if provided)
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = cloudinaryService.uploadImage(image);
        }

        // Create and save the SponsorAd
        SponsorAd sponsorAd = new SponsorAd();
        sponsorAd.setTitle(sponsorAdRequest.title());
        sponsorAd.setDesign(imageUrl);
        sponsorAd.setContent(sponsorAdRequest.content());
        sponsorAd.setDesign_colors(sponsorAdRequest.designColors());
        sponsorAd.setUserId(connectedUser.getName());  // Link ad to sponsor
        sponsorAd = sponsorAdRepository.saveAndFlush(sponsorAd);  // Ensure it's persisted

        // Create the Sponsorship
        Sponsorship sponsorship = new Sponsorship();
        sponsorship.setUserId(connectedUser.getName());
        sponsorship.setSponsorOffer(sponsorOffer);
        sponsorship.setStatus(SponsorshipStatus.PENDING);

        // Establish bidirectional relationship
        sponsorship.getSponsorAds().add(sponsorAd);
        sponsorAd.getSponsorships().add(sponsorship);

        // Save Sponsorship (this also updates SponsorAd due to cascading)
        sponsorship = sponsorshipRepository.save(sponsorship);

        return sponsorship.getId();
    }
    public Integer updateChosenSponsorOffer(Integer oldOfferId, Integer newOfferId, Authentication connectedUser) {

        // Find the Sponsorship that has the oldOfferId
        Sponsorship sponsorship = sponsorshipRepository.findBySponsorOfferIdAndUserId(oldOfferId, connectedUser.getName())
                .orElseThrow(() -> new EntityNotFoundException("No sponsorship found with the provided old offer ID for this user"));

        // Ensure the authenticated sponsor is the owner of the sponsorship
        if (!sponsorship.getCreatedBy().equals(connectedUser.getName())) {
            throw new RuntimeException("You are not authorized to update this sponsorship");
        }

        // Find the new SponsorOffer
        SponsorOffer newSponsorOffer = sponsorOfferRepository.findById(newOfferId)
                .orElseThrow(() -> new EntityNotFoundException("Sponsor Offer not found"));

        // Business Rule: Only allow updates if sponsorship is PENDING or REJECTED
        if (sponsorship.getStatus() != SponsorshipStatus.PENDING
                && sponsorship.getStatus() != SponsorshipStatus.REJECTED) {
            throw new OperationNotPermittedException("You cannot update an offer for an approved or completed sponsorship");
        }

        // Update the sponsorship with the new sponsor offer
        sponsorship.setSponsorOffer(newSponsorOffer);

        // Save the updated sponsorship
        sponsorshipRepository.save(sponsorship);

        return sponsorship.getId();
    }
    @CacheEvict(value = "sponsorOffers", allEntries = true)
    public Integer createSponsorOffer(SponsorOfferRequest request, List<MultipartFile> images, Authentication connectedUser) {

        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile image : images) {
            String imageUrl = cloudinaryService.uploadImage(image);  // Upload to Cloudinary
            imageUrls.add(imageUrl);
        }

        // Create and save the SponsorOffer
        SponsorOffer sponsorOffer = new SponsorOffer();
        sponsorOffer.setTitle(request.title());
        sponsorOffer.setDescription(request.description());
        sponsorOffer.setPrice(request.price());
        sponsorOffer.setProductType(request.productType());
        sponsorOffer.setProductQuantity(request.productQuantity());
        sponsorOffer.setExplainImages(imageUrls);
        sponsorOffer.setStatus(request.status());
        sponsorOffer.setCategory(request.category());
        sponsorOffer.setNumberAds(request.numberAds());
        sponsorOffer.setSalesArea(request.salesArea());
        sponsorOffer.setUserId(connectedUser.getName());  // Link offer to admin who created it


        Integer sponsorOfferId = sponsorOfferRepository.save(sponsorOffer).getId();
        // Publish event to Kafka
        kafkaProducerService.sendMessage("sponsorOfferTopic", "New sponsor offer created with ID: " + sponsorOfferId);

        return sponsorOfferId;

    }

    @CacheEvict(value = "sponsorOffers", allEntries = true)
    public SponsorOfferResponse updateSponsorOffer(Integer offerId, SponsorOfferRequest request, List<MultipartFile> images) {
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
            throw new OperationNotPermittedException("You cannot update an offer with an active or unfinished sponsorship");
        }
            List<String> newImageUrls = new ArrayList<>();
            if (images != null && !images.isEmpty()) {
                // Upload new images to Cloudinary
                for (MultipartFile image : images) {
                    String imageUrl = cloudinaryService.uploadImage(image);
                    newImageUrls.add(imageUrl);
                }
            }

            // **Update SponsorOffer**
            if (!newImageUrls.isEmpty()) {
                sponsorOffer.setExplainImages(newImageUrls); // Replace old images
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
        if (request.numberAds() != null) sponsorOffer.setNumberAds(request.numberAds());
        if (request.productQuantity() != null) sponsorOffer.setProductQuantity(request.productQuantity());
        if (request.productType() != null) sponsorOffer.setProductType(request.productType());
        if (request.salesArea() != null) sponsorOffer.setSalesArea(request.salesArea());




        // Update SponsorOffer status if provided
        if (request.status() != null) sponsorOffer.setStatus(request.status());

        // Save the updated SponsorOffer
        sponsorOfferRepository.save(sponsorOffer);

        return sponsorOfferMapper.toResponseWithImagesUrls(sponsorOffer);
    }

    @CacheEvict(value = "sponsorOffers", allEntries = true)
    public void deleteSponsorOffer(Integer offerId) {
        // Retrieve the SponsorOffer
        SponsorOffer sponsorOffer = sponsorOfferRepository.findById(offerId)
                .orElseThrow(() -> new EntityNotFoundException("Sponsor Offer not found"));

        // Fetch associated Sponsorships
        List<Sponsorship> sponsorships = sponsorshipRepository.findBySponsorOffer(sponsorOffer);

        // Extract Cloudinary image URL
        List<String> imageUrls = sponsorOffer.getExplainImages(); // Ensure this field exists

        // If no Sponsorships exist, delete immediately
        // If no Sponsorships exist, delete immediately
        if (sponsorships == null || sponsorships.isEmpty()) {
            cloudinaryService.deleteImagesFromCloudinary(imageUrls);
            sponsorOfferRepository.delete(sponsorOffer);
            return;
        }

        // Check if all related Sponsorships have a valid status (PENDING, REJECTED, or FINISHED)
        boolean canDelete = sponsorships.stream()
                .allMatch(sponsorship -> sponsorship.getStatus() == SponsorshipStatus.PENDING
                        || sponsorship.getStatus() == SponsorshipStatus.REJECTED
                        || sponsorship.getStatus() == SponsorshipStatus.FINISHED);

        if (!canDelete) {
            throw new OperationNotPermittedException("Cannot delete a Sponsor Offer with active or ongoing sponsorships");
        }

        // Update all related Sponsorships to FINISHED
        sponsorships.forEach(sponsorship -> sponsorship.setStatus(SponsorshipStatus.FINISHED));
        sponsorshipRepository.saveAll(sponsorships);

        cloudinaryService.deleteImagesFromCloudinary(imageUrls);

        // Delete the SponsorOffer
        sponsorOfferRepository.delete(sponsorOffer);

    }


}

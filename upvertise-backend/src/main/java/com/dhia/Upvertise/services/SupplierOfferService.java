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
import com.dhia.Upvertise.notification.Notification;
import com.dhia.Upvertise.notification.NotificationService;
import com.dhia.Upvertise.notification.NotificationStatus;
import com.dhia.Upvertise.repositories.SponsorAdRepository;
import com.dhia.Upvertise.repositories.SupplierOfferRepository;
import com.dhia.Upvertise.repositories.SupplierTransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierOfferService {
    private final SupplierOfferRepository supplierOfferRepository;
    private final SponsorAdRepository sponsorAdRepository;
    private final SupplierTransactionRepository suppliertransactionRepository;
    private final CloudinaryService  cloudinaryService;
    private final SupplierTransactionMapper supplierTransactionMapper;
    private final NotificationService notificationService;
    private final KafkaProducerService kafkaProducerService;


    /*@Transactional
    @Cacheable(value = "supplierOffers", key = "'all:' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public PageResponse<SupplierOfferResponse> getAllSupplierOffers(Pageable pageable) {

        Page<SupplierOffer> page = supplierOfferRepository.findAll(pageable);
        List<SupplierOfferResponse> content = page.getContent().stream()
                .map(SupplierOfferMapper::toResponseWithImageUrl)
                .toList(); // Java 17
        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }*/
    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(role));
    }

    @Transactional
    @Cacheable(value = "supplierOffers", key = "'all:' + #pageable.pageNumber + '-' + #pageable.pageSize + ':' + #authentication.name")
    public PageResponse<SupplierOfferResponse> getAllSupplierOffers(Pageable pageable, Authentication authentication) {
        boolean isAdmin = hasRole(authentication, "ROLE_Admin");
        boolean isSupplier = hasRole(authentication, "ROLE_Supplier");
        boolean isProvider = hasRole(authentication, "ROLE_Provider");

        Page<SupplierOffer> page;

        if (isAdmin || isSupplier) {
            page = supplierOfferRepository.findAll(pageable);
        } else if (isProvider) {
            String providerId = authentication.getName(); // Keycloak user ID
            page = supplierOfferRepository.findByCreatedBy(providerId, pageable);
        } else {
            throw new AccessDeniedException("Unauthorized access");
        }

        List<SupplierOfferResponse> content = page.getContent().stream()
                .map(SupplierOfferMapper::toResponseWithImageUrl)
                .toList();

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
    @Cacheable(value = "supplierOffers", key = "'status:' + #status + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public PageResponse<SupplierOfferResponse> getSupplierOfferByStatus(SupplierOfferStatus status, Pageable pageable) {
        Page<SupplierOffer> page = supplierOfferRepository.findByStatus(status, pageable);
        List<SupplierOfferResponse> content = page.getContent().stream()
                .map(SupplierOfferMapper::toResponseWithImageUrl)
                .toList(); // Java 17
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
    @Caching(evict = {
            @CacheEvict(value = "supplierOffers", key = "'all:' + #request.pageNumber + '-' + #request.pageSize", allEntries = true),
            @CacheEvict(value = "supplierOffers", key = "'status:' + #request.status + '-' + #request.pageNumber + '-' + #request.pageSize", allEntries = true)
    })
    public SupplierOfferResponse createSupplierOffer(SupplierOfferRequest request , MultipartFile image) {
        List<SponsorAd> sponsorAds = sponsorAdRepository.findAllById(request.sponsorAdIds());
        SupplierOffer supplierOffer = SupplierOfferMapper.toSupplierOffer(request,sponsorAds);
        // Upload the image to Cloudinary and set the URL
        String imageUrl = cloudinaryService.uploadImage(image);
        supplierOffer.setImageUrl(imageUrl);
        SupplierOffer savedOffer = supplierOfferRepository.save(supplierOffer);
        // Publish event to Kafka
        kafkaProducerService.sendMessage("supplierNotificationTopic", "A new supplier offer has been successfully created : " + savedOffer.getTitle());

        // Create the notification
        Notification notification = Notification.builder()
                .status(NotificationStatus.SUPPLIER_OFFER_CREATED)

                .message("A new supplier offer has been successfully created with ID: " + savedOffer.getId())
                .build();

        // Notify the supplier who created it and admin
        notificationService.sendNotification(supplierOffer.getCreatedBy(), notification);
        notificationService.sendNotificationToRole("Supplier",notification);
        notificationService.sendNotificationToRole("Admin",notification);
        return SupplierOfferMapper.toResponse(savedOffer);
    }
    @Caching(evict = {
            @CacheEvict(value = "supplierOffers", allEntries = true)
    })
    public SupplierOfferResponse updateSupplierOffer(Integer id, SupplierOfferRequest request, MultipartFile image) {
        SupplierOffer existingOffer = supplierOfferRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SupplierOffer not found"));
        // If a new image is provided, upload it and update the URL
        if (image != null && !image.isEmpty()) {
            // Delete old image from Cloudinary (optional but recommended)
            if (existingOffer.getImageUrl() != null) {
                cloudinaryService.deleteImage(existingOffer.getImageUrl());
            }

            // Upload new image and set new URL
            String newImageUrl = cloudinaryService.uploadImage(image);
            existingOffer.setImageUrl(newImageUrl);
        }
        SupplierOfferMapper.updateSupplierOffer(existingOffer, request);

        // 4) **NEW: Update the SponsorAds relationship**
        //    - fetch all SponsorAd entities by IDs
        List<SponsorAd> ads = sponsorAdRepository.findAllById(request.sponsorAdIds());
        if (ads.size() != request.sponsorAdIds().size()) {
            throw new EntityNotFoundException("One or more SponsorAds not found");
        }
        existingOffer.setSponsorAds(new HashSet<>(ads));
        SupplierOffer updatedOffer = supplierOfferRepository.save(existingOffer);
        // Publish event to Kafka
        kafkaProducerService.sendMessage("supplierNotificationTopic", "A supplier offer has been successfully updated : " + updatedOffer.getTitle());

        // Create the notification
        Notification notification = Notification.builder()
                .status(NotificationStatus.SUPPLIER_OFFER_UPDATED)

                .message("A supplier offer has been successfully updated with ID: " + updatedOffer.getId())
                .build();

        // Notify the supplier who created it and admin
        notificationService.sendNotification(updatedOffer.getCreatedBy(), notification);
        notificationService.sendNotificationToRole("Supplier",notification);
        return SupplierOfferMapper.toResponse(updatedOffer);
    }
    @CacheEvict(value = "supplierOffers", allEntries = true)
    public void deleteSupplierOffer(Integer id) {
        SupplierOffer existingOffer = supplierOfferRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SupplierOffer not found"));
        // Delete the image from Cloudinary if it exists
        if (existingOffer.getImageUrl() != null && !existingOffer.getImageUrl().isEmpty()) {
            cloudinaryService.deleteImage(existingOffer.getImageUrl());
        }
        supplierOfferRepository.delete(existingOffer);
        // Publish event to Kafka
        kafkaProducerService.sendMessage("supplierNotificationTopic", "A supplier offer has been deleted : " + existingOffer.getTitle());

        // Create the notification
        Notification notification = Notification.builder()
                .status(NotificationStatus.SUPPLIER_OFFER_DELETED)
                .message("A supplier offer has been deleted with ID: " + existingOffer.getId())
                .build();

        // Notify the supplier who created it and admin
        notificationService.sendNotification(existingOffer.getCreatedBy(), notification);
        notificationService.sendNotificationToRole("Supplier",notification);
    }
    @Transactional
    public List<SupplierTransactionResponse> chooseSupplierOffer(
            Authentication connectedUser,
            Integer supplierOfferId
    ) {
        SupplierOffer offer = supplierOfferRepository.findById(supplierOfferId)
                .orElseThrow(() -> new EntityNotFoundException("SupplierOffer not found"));

        Set<SponsorAd> sponsorAds = offer.getSponsorAds();
        if (sponsorAds == null || sponsorAds.isEmpty()) {
            throw new EntityNotFoundException("No SponsorAds found for SupplierOffer");
        }

        List<SupplierTransactionResponse> responses = new ArrayList<>();

        for (SponsorAd ad : sponsorAds) {
            Set<Sponsorship> sponsorships = ad.getSponsorships();
            if (sponsorships == null || sponsorships.isEmpty()) {
                // Fail fast: no sponsorships means we cannot proceed
                throw new EntityNotFoundException(
                        String.format("SponsorAd (id=%d) has no associated Sponsorships", ad.getId())
                );
            }

            for (Sponsorship sponsorship : sponsorships) {
                SupplierTransaction tx = new SupplierTransaction();
                tx.setUserId(connectedUser.getName());
                tx.setSupplierOffer(offer);
                tx.setSupplierTransactionStatus(SupplierTransactionStatus.PENDING);
                tx.setSponsorship(sponsorship);
                // tx.setProofs(...) if needed

                SupplierTransaction saved = suppliertransactionRepository.save(tx);
                // Publish event to Kafka
                kafkaProducerService.sendMessage("providerNotificationTopic", "New transactions have been created for Supplier Offer  " + offer.getTitle());
                kafkaProducerService.sendMessage("adminNotificationTopic", "New transactions have been created for Supplier Offer " + offer.getTitle());

                // Create the notification
                Notification notification = Notification.builder()
                        .status(NotificationStatus.SUPPLIER_TRANSACTION_CREATED)
                        .message("New transactions have been created for Supplier Offer" + offer.getTitle())
                        .build();

                // Notify the supplier who created it and admin
                notificationService.sendNotification(saved.getCreatedBy(), notification);
                notificationService.sendNotificationToRole("Provider",notification);
                notificationService.sendNotificationToRole("Admin",notification);

                responses.add(supplierTransactionMapper.toSupplierTransactionResponse(saved));
            }
        }

        if (responses.isEmpty()) {
            throw new IllegalStateException("No Sponsorships available to create transactions");
        }

        return responses;
    }

    /*public SupplierTransactionResponse chooseSupplierOffer(Authentication connectedUser, Integer supplierOfferId ) {
        SupplierOffer supplierOffer = supplierOfferRepository.findById(supplierOfferId)
                .orElseThrow(() -> new EntityNotFoundException("SupplierOffer not found"));

        // Get all SponsorAds associated with the SupplierOffer
        Set<SponsorAd> sponsorAds = supplierOffer.getSponsorAds();
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
//        transaction.setProofs(proofUrls);  // Store uploaded proof URLs
        SupplierTransaction savedTransaction = suppliertransactionRepository.save(transaction);
        return supplierTransactionMapper.toSupplierTransactionResponse(savedTransaction);

    }*/

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
        return supplierTransactionMapper.toSupplierTransactionResponse(updatedTransaction);
    }

}

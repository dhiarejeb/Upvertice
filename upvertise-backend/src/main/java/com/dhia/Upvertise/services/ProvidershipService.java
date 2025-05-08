package com.dhia.Upvertise.services;

import com.dhia.Upvertise.dto.ProvidershipRequest;
import com.dhia.Upvertise.dto.ProvidershipResponse;
import com.dhia.Upvertise.dto.SponsorshipResponse;
import com.dhia.Upvertise.mapper.ProvidershipMapper;
import com.dhia.Upvertise.mapper.SponsorshipMapper;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.models.provider.Providership;
import com.dhia.Upvertise.models.provider.ProvidershipApprovalStatus;
import com.dhia.Upvertise.models.provider.ProvidershipStatus;
import com.dhia.Upvertise.models.sponsorship.Sponsorship;
import com.dhia.Upvertise.notification.Notification;
import com.dhia.Upvertise.notification.NotificationService;
import com.dhia.Upvertise.notification.NotificationStatus;
import com.dhia.Upvertise.repositories.ProvidershipRepository;
import com.dhia.Upvertise.repositories.SponsorshipRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProvidershipService {

    private final ProvidershipRepository providershipRepository;

    private final SponsorshipRepository sponsorshipRepository;

    private final CloudinaryService cloudinaryService;
    private final ProvidershipMapper providershipMapper;
    private final NotificationService notificationService;
    private final KafkaProducerService kafkaProducerService;




    public PageResponse<ProvidershipResponse> getProviderships(
            Authentication connectedUser,
            Pageable pageable) {
        String userId = connectedUser.getName(); // Directly get Keycloak user ID
        boolean isAdmin = hasRole(connectedUser, "ROLE_Admin");
        boolean isProvider = hasRole(connectedUser, "ROLE_Provider");

        Page<Providership> providershipPage;

        if (isAdmin) {
            providershipPage = providershipRepository.findAll(pageable);
        } else if (isProvider) {
            providershipPage = providershipRepository.findByUserId(userId, pageable);
        } else {
            throw new SecurityException("Access denied. Only admins and providers can access providerships.");
        }

        List<ProvidershipResponse> content = providershipPage.getContent().stream()
                .map(providershipMapper::toProvidershipResponse)
                .collect(Collectors.toList());

        return PageResponse.<ProvidershipResponse>builder()
                .content(content)
                .number(providershipPage.getNumber())
                .size(providershipPage.getSize())
                .totalElements(providershipPage.getTotalElements())
                .totalPages(providershipPage.getTotalPages())
                .first(providershipPage.isFirst())
                .last(providershipPage.isLast())
                .build();
    }

    public ProvidershipResponse getProvidershipById(Authentication connectedUser, Integer providershipId){
        String userId = connectedUser.getName();
        boolean isAdmin = connectedUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_Admin"));

        // Fetch providership or throw 404
        Providership providership = providershipRepository.findById(providershipId)
                .orElseThrow(() -> new ResourceNotFoundException("Providership not found"));

        // Authorization check
        if (!isAdmin && !providership.getCreatedBy().equals(userId)) {
            throw new AccessDeniedException("You don't have permission to access this providership");
        }

        return providershipMapper.toProvidershipResponse(providership);
    }

    /**
     * Checks if the current user has a specific role.
     * @param connectedUser Authenticated user.
     * @param role The role to check (e.g., "ROLE_ADMIN", "ROLE_PROVIDER").
     * @return True if user has the role, otherwise false.
     */
    private boolean hasRole(Authentication connectedUser, String role) {
        return connectedUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(role));
    }


    public void deleteProvidership(Integer id, Authentication authentication) {
        String userId = authentication.getName();
        boolean isAdmin = hasRole(authentication, "ROLE_Admin");
        boolean isProvider = hasRole(authentication, "ROLE_Provider");

        Optional<Providership> optionalProvidership = providershipRepository.findById(id);
        if (optionalProvidership.isEmpty()) {
            throw new IllegalArgumentException("Providership not found with ID: " + id);
        }

        Providership providership = optionalProvidership.get();

        if (isAdmin) {
            providershipRepository.delete(providership);
        } else if (isProvider) {
            if (!providership.getCreatedBy().equals(userId)) {
                throw new SecurityException("Access denied. Providers can only delete their own providerships.");
            }
            if (providership.getProvidershipApprovalStatus() == ProvidershipApprovalStatus.APPROVED) {
                throw new IllegalStateException("Cannot delete an approved providership.");
            }
            deleteProofsFromCloudinary(providership.getProofDocs()); // Delete proofs first
            providershipRepository.delete(providership);

        } else {
            throw new SecurityException("Access denied. Only admins and providers can delete providerships.");
        }
        // === Kafka Notifications ===
        String kafkaMessage = "Providership deleted: ID " + id;
        //kafkaProducerService.sendMessage("adminNotificationTopic", kafkaMessage);
       // kafkaProducerService.sendMessage("providerNotificationTopic", kafkaMessage);

// === WebSocket Notifications ===
        Notification notification = Notification.builder()
                .status(NotificationStatus.PROVIDERSHIP_CREATED) // Adjust status as needed
                .message("The providership with ID " + id + " has been deleted.")
                .build();

        notificationService.sendNotification(providership.getUserId(), notification);
        notificationService.sendNotificationToRole("Admin", notification);
    }
    public ProvidershipResponse updateProvidership(
            Integer id,
            ProvidershipRequest request,
            List<MultipartFile> proofFiles,
            Authentication connectedUser) {

        Providership p = providershipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Providership not found"));

        String userId = connectedUser.getName();
        boolean isAdmin    = hasRole(connectedUser, "ROLE_Admin");
        boolean isProvider = hasRole(connectedUser, "ROLE_Provider")
                && p.getCreatedBy().equals(userId);
        if (p.getSponsorship() != null) {
            Integer productQuantity = p.getSponsorship()
                    .getSponsorOffer()
                    .getProductQuantity();
            p.setTotalProduct(productQuantity);
        }
        if (!isAdmin && !isProvider) {
            throw new AccessDeniedException("You do not have permission to update this providership.");
        }

        if (isAdmin) {
            // Admin can only change approval status, bonus, and reassign sponsorship
            if (request.providershipApprovalStatus() != null) {
                p.setProvidershipApprovalStatus(request.providershipApprovalStatus());
            }
            if (request.bonusEarned() != null) {
                p.setBonusEarned(request.bonusEarned());
            }
            if (request.sponsorshipId() != null) {
                Sponsorship s = sponsorshipRepository.findById(request.sponsorshipId())
                        .orElseThrow(() -> new EntityNotFoundException("Sponsorship not found"));
                p.setSponsorship(s);
            }
        } else {
            // Provider can update all other fields at any time
            if (request.status() != null) {
                p.setStatus(request.status());
            }
            if (request.producedProduct() != null) {
                p.setProducedProduct(request.producedProduct());
            }
            //if (request.totalProduct() != null) {
                //p.setTotalProduct(request.totalProduct());
            //}
            // 🔄 Auto-update totalProduct if Sponsorship exists

            if (request.location() != null) {
                p.setLocation(request.location());
            }
            if (request.hasPrintMachine() != null) {
                p.setHasPrintMachine(request.hasPrintMachine());
            }
            if (request.providedProductTypes() != null) {
                p.setProvidedProductTypes(request.providedProductTypes());
            }
            if (proofFiles != null && !proofFiles.isEmpty()) {
                p.setProofDocs(uploadProofs(proofFiles));
            }
        }

        providershipRepository.save(p);
        // === Kafka Notifications ===
        String kafkaMessage = "Providership updated: ID " + id + " to status: " + p.getStatus();
        //kafkaProducerService.sendMessage("adminNotificationTopic", kafkaMessage);
        //kafkaProducerService.sendMessage("providerNotificationTopic", kafkaMessage);

// === WebSocket Notifications ===
        Notification notification = Notification.builder()
                .status(NotificationStatus.PROVIDERSHIP_UPDATED) // Adjust status as needed
                .message("The providership with ID " + id + " was updated to status " + p.getStatus())
                .build();

        notificationService.sendNotification(p.getUserId(), notification);
        notificationService.sendNotificationToRole("Admin", notification);
        //notificationService.sendNotificationToRole("Provider", notification);

        return providershipMapper.toProvidershipResponse(p);

    }


    public ProvidershipResponse createProvidership(ProvidershipRequest request , List<MultipartFile> proofFiles, Authentication  connectedUser) {
        String userId = connectedUser.getName(); // Get provider's Keycloak ID

        // Create a new Providership
        Providership providership = new Providership();
        providership.setUserId(userId);
        providership.setTotalProduct(request.totalProduct());
        providership.setProducedProduct(0); // Starts at 0
        providership.setBonusEarned(0.0); // No bonus yet
        providership.setStatus(ProvidershipStatus.PENDING);
        providership.setProvidershipApprovalStatus(ProvidershipApprovalStatus.PENDING);
        // ✅ Store Proof Docs in Cloudinary (optional)
        List<String> uploadedProofs = new ArrayList<>();
        if (proofFiles != null && !proofFiles.isEmpty()) {
            uploadedProofs = uploadProofs(proofFiles);
        }
        providership.setProofDocs(uploadedProofs);
        // Add new fields
        providership.setTotalProduct(request.totalProduct() != null ? request.totalProduct() : 0);
        providership.setLocation(request.location() != null ? request.location() : "");
        providership.setHasPrintMachine(request.hasPrintMachine() != null ? request.hasPrintMachine() : false);

        // Save it
        providership = providershipRepository.save(providership);
// === Kafka Notifications ===
        String kafkaMessage = "New providership created: ID " + providership.getId();
        kafkaProducerService.sendMessage("adminNotificationTopic", kafkaMessage);
        //kafkaProducerService.sendMessage("providerNotificationTopic", kafkaMessage);

// === WebSocket Notifications ===
        Notification notification = Notification.builder()
                .status(NotificationStatus.PROVIDERSHIP_CREATED) // Adjust status as needed
                .message("A new providership with ID " + providership.getId() + " has been created and is pending approval.")
                .build();

        notificationService.sendNotification(providership.getUserId(), notification);
        notificationService.sendNotificationToRole("Admin", notification);
        //notificationService.sendNotificationToRole("Provider", notification);

        // Convert to DTO and return
        return providershipMapper.toProvidershipResponse(providership);

    }

    // ✅ Upload images to Cloudinary and return URLs
    private List<String> uploadProofs(List<MultipartFile> proofFiles) {
        if (proofFiles == null || proofFiles.isEmpty()) return new ArrayList<>();
        return proofFiles.stream()
                .filter(file -> !file.isEmpty())
                .map(cloudinaryService::uploadImage) // Upload each file and get URL
                .collect(Collectors.toList());
    }
    // ✅ Helper function to delete images from Cloudinary
    private void deleteProofsFromCloudinary(List<String> proofDocs) {
        if (proofDocs == null || proofDocs.isEmpty()) return;
        proofDocs.forEach(cloudinaryService::deleteImage); // Assuming you have a service for deleting Cloudinary images
    }


}

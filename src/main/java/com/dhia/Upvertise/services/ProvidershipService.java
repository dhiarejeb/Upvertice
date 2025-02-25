package com.dhia.Upvertise.services;

import com.dhia.Upvertise.dto.ProvidershipRequest;
import com.dhia.Upvertise.dto.ProvidershipResponse;
import com.dhia.Upvertise.mapper.ProvidershipMapper;
import com.dhia.Upvertise.models.common.PageResponse;
import com.dhia.Upvertise.models.provider.Providership;
import com.dhia.Upvertise.models.provider.ProvidershipApprovalStatus;
import com.dhia.Upvertise.models.provider.ProvidershipStatus;
import com.dhia.Upvertise.models.sponsorship.Sponsorship;
import com.dhia.Upvertise.repositories.ProvidershipRepository;
import com.dhia.Upvertise.repositories.SponsorshipRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProvidershipService {

    private final ProvidershipRepository providershipRepository;

    private final SponsorshipRepository sponsorshipRepository;



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
                .map(ProvidershipMapper::toProvidershipResponse)
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
            providershipRepository.delete(providership);
        } else {
            throw new SecurityException("Access denied. Only admins and providers can delete providerships.");
        }
    }

    public ProvidershipResponse updateProvidership(Integer id, ProvidershipRequest request, Authentication connectedUser) {
        Providership providership = providershipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Providership not found"));

        String userId = connectedUser.getName();
        boolean isAdmin = hasRole(connectedUser, "ROLE_Admin");
        boolean isProvider = hasRole(connectedUser, "ROLE_Provider");

        if (!isAdmin && !isProvider) {
            throw new AccessDeniedException("You do not have permission to update this providership.");
        }

        if (isAdmin) {
            // Admin updates only approval status and bonus earned
            providership.setProvidershipApprovalStatus(request.providershipApprovalStatus() == null
                    ? providership.getProvidershipApprovalStatus()
                    : ProvidershipApprovalStatus.valueOf(request.providershipApprovalStatus().name()));

            providership.setBonusEarned(request.bonusEarned() == null
                    ? providership.getBonusEarned()
                    : request.bonusEarned());
            // ✅ Assign Sponsorship (Admin Only)
            if (request.sponsorshipId() != null) {
                Sponsorship sponsorship = sponsorshipRepository.findById(request.sponsorshipId())
                        .orElseThrow(() -> new EntityNotFoundException("Sponsorship not found"));
                providership.setSponsorship(sponsorship);
            }
        } else if (isProvider && (providership.getCreatedBy().equals(userId))) {
            // Prevent updates if providership is already approved
            if (!(providership.getProvidershipApprovalStatus() == ProvidershipApprovalStatus.APPROVED)) {

                providership.setTotalProduct(request.totalProduct() == null
                        ? providership.getTotalProduct()
                        : request.totalProduct());
                providership.setLocation(request.location() == null
                        ? providership.getLocation()
                        : request.location());
                providership.setHasPrintMachine(request.hasPrintMachine() == null
                        ? providership.getHasPrintMachine()
                        : request.hasPrintMachine());
            }else {

                // Provider updates only non-admin fields
                providership.setStatus(request.status() == null
                        ? providership.getStatus()
                        : request.status());

                providership.setProducedProduct(request.producedProduct() == null
                        ? providership.getProducedProduct()
                        : request.producedProduct());


                providership.setProofDocs(request.proofDocs() == null
                        ? providership.getProofDocs()
                        : request.proofDocs());
            }

        }

        providershipRepository.save(providership);
        return ProvidershipMapper.toProvidershipResponse(providership);
    }

    public ProvidershipResponse createProvidership(ProvidershipRequest request , Authentication  connectedUser) {
        String userId = connectedUser.getName(); // Get provider's Keycloak ID

        // Create a new Providership
        Providership providership = new Providership();
        providership.setUserId(userId);
        providership.setTotalProduct(request.totalProduct());
        providership.setProducedProduct(0); // Starts at 0
        providership.setBonusEarned(0.0); // No bonus yet
        providership.setStatus(ProvidershipStatus.PENDING);
        providership.setProvidershipApprovalStatus(ProvidershipApprovalStatus.PENDING);
        providership.setProofDocs(new ArrayList<>()); // Empty initially
        // Add new fields
        providership.setLocation(request.location());
        providership.setHasPrintMachine(request.hasPrintMachine());

        // Save it
        providership = providershipRepository.save(providership);

        // Convert to DTO and return
        return ProvidershipMapper.toProvidershipResponse(providership);
    }


}

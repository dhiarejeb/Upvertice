package com.dhia.Upvertise.dto;

import com.dhia.Upvertise.models.provider.ProvidershipApprovalStatus;
import com.dhia.Upvertise.models.provider.ProvidershipStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ProvidershipResponse(
        Integer id,
        SponsorshipLightResponse sponsorship, // Now contains details
        String userId,
        ProvidershipStatus status,
        Integer producedProduct,
        Integer totalProduct,
        Double bonusEarned,
        ProvidershipApprovalStatus providershipApprovalStatus,
        List<String> proofDocs,
        String location,
        Boolean hasPrintMachine,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate

) {}

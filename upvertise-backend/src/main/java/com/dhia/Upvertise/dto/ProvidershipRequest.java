package com.dhia.Upvertise.dto;

import com.dhia.Upvertise.models.provider.ProvidershipApprovalStatus;
import com.dhia.Upvertise.models.provider.ProvidershipStatus;

import java.util.List;

public record ProvidershipRequest(
        Integer sponsorshipId,
        String userId,
        ProvidershipStatus status,
        Integer producedProduct,
        Integer totalProduct,
        Double bonusEarned,
        ProvidershipApprovalStatus providershipApprovalStatus,
        //List<String> proofDocs,
        String location, // Provider's location
        Boolean hasPrintMachine // Does the provider have a print machine?
) {
}

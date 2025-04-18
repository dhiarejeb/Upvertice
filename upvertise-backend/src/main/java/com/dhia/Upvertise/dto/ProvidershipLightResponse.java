package com.dhia.Upvertise.dto;

import com.dhia.Upvertise.models.provider.ProvidershipApprovalStatus;
import com.dhia.Upvertise.models.provider.ProvidershipStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ProvidershipLightResponse(
        //dto having no sponsorship data no supplierTransaction data

        Integer id,
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
) {
}

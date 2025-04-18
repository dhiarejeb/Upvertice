package com.dhia.Upvertise.dto;

import com.dhia.Upvertise.models.provider.ProvidedProductType;
import com.dhia.Upvertise.models.provider.ProvidershipApprovalStatus;
import com.dhia.Upvertise.models.provider.ProvidershipStatus;
import jakarta.validation.constraints.*;

import java.util.Set;


public record ProvidershipRequest(

        Integer sponsorshipId, // Optional, only needed for update (Admin)

        String userId, // Optional, will be taken from auth for create

        ProvidershipStatus status, // Optional, default to PENDING if not provided

        @PositiveOrZero(message = "Produced product count must be zero or positive")
        Integer producedProduct, // Default to 0 if not provided

        @Positive(message = "Total product must be positive")
        Integer totalProduct, // Optional, default to 0 or can be provided by the user

        @PositiveOrZero(message = "Bonus earned must be zero or positive")
        Double bonusEarned, // Default to 0.0 if not provided

        ProvidershipApprovalStatus providershipApprovalStatus, // Optional, default to PENDING if not provided

        @NotBlank(message = "Location cannot be blank")
        String location, // Required

        @NotNull(message = "Has print machine field is required")
        Boolean hasPrintMachine, // Required

        @NotEmpty(message = "At least one provided product type must be specified")
        Set<ProvidedProductType> providedProductTypes // Required
) {
        // Constructor to provide default values for some fields if not provided
        public ProvidershipRequest {
                // Default values for optional fields if not set
                if (producedProduct == null) producedProduct = 0;
                if (totalProduct == null) totalProduct = 0;
                if (bonusEarned == null) bonusEarned = 0.0;
                if (status == null) status = ProvidershipStatus.PENDING;
                if (providershipApprovalStatus == null) providershipApprovalStatus = ProvidershipApprovalStatus.PENDING;
                if (location == null) location = "";
                if (hasPrintMachine == null) hasPrintMachine = false;
        }
}

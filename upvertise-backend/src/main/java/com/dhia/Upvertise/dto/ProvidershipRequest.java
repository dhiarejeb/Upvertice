package com.dhia.Upvertise.dto;

import com.dhia.Upvertise.models.provider.ProvidershipApprovalStatus;
import com.dhia.Upvertise.models.provider.ProvidershipStatus;
import jakarta.validation.constraints.*;


public record ProvidershipRequest(
        @NotNull(message = "Sponsorship ID is required") Integer sponsorshipId,

        @NotBlank(message = "User ID cannot be blank") String userId,

        @NotNull(message = "Providership status is required") ProvidershipStatus status,

        @NotNull(message = "Produced product count is required")
        @PositiveOrZero(message = "Produced product count must be zero or positive") Integer producedProduct,

        @NotNull(message = "Total product count is required")
        @Positive(message = "Total product must be positive") Integer totalProduct,

        @NotNull(message = "Bonus earned is required")
        @PositiveOrZero(message = "Bonus earned must be zero or positive") Double bonusEarned,

        @NotNull(message = "Providership approval status is required") ProvidershipApprovalStatus providershipApprovalStatus,

        @NotBlank(message = "Location cannot be blank") String location,

        @NotNull(message = "Has print machine field is required") Boolean hasPrintMachine
) {
}

package com.dhia.Upvertise.dto;

import com.dhia.Upvertise.models.provider.ProvidershipApprovalStatus;
import com.dhia.Upvertise.models.provider.ProvidershipStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Schema(description = "Response object containing providership data")
public record ProvidershipResponse(

        @Schema(description = "Providership ID", example = "456")
        Integer id,

        @Schema(description = "Associated sponsorship details")
        SponsorshipLightResponse sponsorship,

        @Schema(description = "User ID of the provider", example = "user-abc-123")
        String userId,

        @Schema(description = "Providership status", example = "PENDING")
        ProvidershipStatus status,

        @Schema(description = "Number of products produced", example = "100")
        Integer producedProduct,

        @Schema(description = "Total number of products to be produced", example = "500")
        Integer totalProduct,

        @Schema(description = "Bonus earned", example = "150.0")
        Double bonusEarned,

        @Schema(description = "Admin approval status", example = "PENDING")
        ProvidershipApprovalStatus providershipApprovalStatus,

        @Schema(description = "Proof document URLs")
        List<String> proofDocs,

        @Schema(description = "Provider location", example = "Tunis")
        String location,

        @Schema(description = "Whether provider owns a print machine", example = "true")
        Boolean hasPrintMachine,

        @Schema(description = "Creation timestamp")
        LocalDateTime createdDate,

        @Schema(description = "Last update timestamp")
        LocalDateTime lastModifiedDate

) {}


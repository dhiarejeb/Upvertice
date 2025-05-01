package com.dhia.Upvertise.dto;

import com.dhia.Upvertise.models.provider.ProvidedProductType;
import com.dhia.Upvertise.models.provider.ProvidershipApprovalStatus;
import com.dhia.Upvertise.models.provider.ProvidershipStatus;
import jakarta.validation.constraints.*;

import java.util.Set;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for creating or updating a providership")
public record ProvidershipRequest(

        @Schema(description = "ID of the related sponsorship (required for update)", example = "123")
        Integer sponsorshipId,

        @Schema(description = "User ID (taken from authentication)", example = "user-abc-123")
        String userId,

        @Schema(description = "Status of the providership", example = "PENDING")
        ProvidershipStatus status,


        @Schema(description = "Number of products already produced", example = "100")
        Integer producedProduct,


        @Schema(description = "Total number of products to produce", example = "500")
        Integer totalProduct,


        @Schema(description = "Bonus earned from providership", example = "150.0")
        Double bonusEarned,

        @Schema(description = "Approval status by the admin", example = "PENDING")
        ProvidershipApprovalStatus providershipApprovalStatus,


        @Schema(description = "Location of the provider", example = "Tunis, Tunisia")
        String location,


        @Schema(description = "Whether the provider has a print machine", example = "true")
        Boolean hasPrintMachine,


        @Schema(description = "Set of provided product types")
        Set<ProvidedProductType> providedProductTypes
) {
        /*public ProvidershipRequest {
                if (producedProduct == null) producedProduct = 0;
                if (totalProduct == null) totalProduct = 0;
                if (bonusEarned == null) bonusEarned = 0.0;
                if (status == null) status = ProvidershipStatus.PENDING;
                if (providershipApprovalStatus == null) providershipApprovalStatus = ProvidershipApprovalStatus.PENDING;
                if (location == null) location = "";
                if (hasPrintMachine == null) hasPrintMachine = false;
        }*/
}


package com.dhia.Upvertise.dto;

import com.dhia.Upvertise.models.sponsorship.SponsorOfferStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Schema(description = "SponsorOfferRequest")
@Builder
public record SponsorOfferRequest(
        @Schema(description = "Title") @JsonProperty("title") String title,
        @Schema(description = "Description") @JsonProperty("description") String description,
        @Schema(description = "Price") @JsonProperty("price") Double price,
        @Schema(description = "Category") @JsonProperty("category") String category,
        @Schema(description = "Product Quantity") @JsonProperty("productQuantity") Integer productQuantity,
        @Schema(description = "Product Type") @JsonProperty("productType") String productType,
        @Schema(description = "Number of Ads") @JsonProperty("numberAds") Integer numberAds,
        @Schema(description = "Status") @JsonProperty("status") SponsorOfferStatus status,
        @Schema(description = "Sales Area") @JsonProperty("salesArea") String salesArea
) {
    @JsonCreator
    public SponsorOfferRequest {
    }
}

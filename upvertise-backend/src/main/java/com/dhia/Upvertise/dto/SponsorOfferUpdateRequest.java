package com.dhia.Upvertise.dto;

import com.dhia.Upvertise.models.sponsorship.SponsorOfferStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record SponsorOfferUpdateRequest(
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("price") Double price,
        @JsonProperty("category") String category,
        @JsonProperty("productQuantity") Integer productQuantity,
        @JsonProperty("productType") String productType,
        @JsonProperty("numberAds") Integer numberAds,
        @JsonProperty("status") SponsorOfferStatus status,
        @JsonProperty("salesArea") String salesArea
) {
    @JsonCreator
    public SponsorOfferUpdateRequest {
    }
}

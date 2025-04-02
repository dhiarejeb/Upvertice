package com.dhia.Upvertise.dto;

import com.dhia.Upvertise.models.sponsorship.SponsorOfferStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record SponsorOfferRequest(
        @JsonProperty("title")
        @NotBlank(message = "Title cannot be blank") String title,

        @JsonProperty("description")
        @NotBlank(message = "Description cannot be blank") String description,

        @JsonProperty("price")
        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive") Double price,

        @JsonProperty("category")
        @NotBlank(message = "Category cannot be blank") String category,

        @JsonProperty("productQuantity")
        @NotNull(message = "Product quantity is required")
        @PositiveOrZero(message = "Product quantity must be zero or positive") Integer productQuantity,

        @JsonProperty("productType")
        @NotBlank(message = "Product type cannot be blank") String productType,

        @JsonProperty("numberAds")
        @NotNull(message = "Number of ads is required")
        @Positive(message = "Number of ads must be positive") Integer numberAds,

        @JsonProperty("status")
        @NotNull(message = "Status is required") SponsorOfferStatus status,

        @JsonProperty("salesArea")
        @NotBlank(message = "Sales area cannot be blank") String salesArea
) {
    @JsonCreator
    public SponsorOfferRequest {
    }
}

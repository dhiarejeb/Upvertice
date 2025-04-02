package com.dhia.Upvertise.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record SponsorOfferResponse(
        String title,
        String description,
        Double price,
        Integer productQuantity,
        String status,
        String category,
        Integer numberAds,
        String productType,
        String salesArea,
        List<String> explainImages

) {
}

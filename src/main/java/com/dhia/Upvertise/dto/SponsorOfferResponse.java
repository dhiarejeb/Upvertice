package com.dhia.Upvertise.dto;

import lombok.Builder;

@Builder
public record SponsorOfferResponse(
        String title,
        String description,
        Double price,
        Integer gobletQuantity,
        String status,
        String explainImage
) {
}

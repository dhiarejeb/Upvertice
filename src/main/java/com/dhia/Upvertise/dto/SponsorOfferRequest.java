package com.dhia.Upvertise.dto;

import com.dhia.Upvertise.models.sponsorship.SponsorOfferStatus;
import lombok.Builder;

@Builder
public record SponsorOfferRequest(
        String title,
        String description,
        Double price,
        String category,
        Integer gobletQuantity,
        String explainImage,
        Integer numberAds,
        SponsorOfferStatus status
) {
}

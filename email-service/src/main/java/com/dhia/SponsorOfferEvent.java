package com.dhia;

public record SponsorOfferEvent(
        Integer offerId,
        String title,
        String createdBy,
        String adminEmail
) {
}

package com.dhia.Upvertise.dto;

import com.dhia.Upvertise.models.sponsorship.SponsorshipStatus;

import java.time.LocalDateTime;
import java.util.Set;

public record SponsorshipResponse(
        Integer id,
        SponsorshipStatus status,
        String userId,
        String sponsorOfferTitle,
        Set<String> sponsorAdTitles,
        LocalDateTime createdAt
) {
}

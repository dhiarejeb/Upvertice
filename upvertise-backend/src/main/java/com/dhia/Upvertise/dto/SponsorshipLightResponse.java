package com.dhia.Upvertise.dto;

import com.dhia.Upvertise.models.sponsorship.SponsorshipStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record SponsorshipLightResponse(
        Integer id,
        SponsorshipStatus status,
        String userId,
        SponsorOfferResponse sponsorOffer,
        Set<SponsorAdResponse> sponsorAds,
        LocalDateTime createdDate,
        List<SupplierTransactionResponse> supplierTransactions
) {
}

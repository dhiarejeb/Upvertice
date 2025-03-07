package com.dhia.Upvertise.dto;

import com.dhia.Upvertise.models.sponsorship.SponsorshipStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record SponsorshipResponse(
        Integer id,
        SponsorshipStatus status,
        String userId,
        SponsorOfferResponse sponsorOffer,
        Set<SponsorAdResponse> sponsorAds,
        LocalDateTime createdDate,
        List<ProvidershipLightResponse> providerships,
        List<SupplierTransactionLightResponse> supplierTransactions
) {}

package com.dhia.Upvertise.dto;

import com.dhia.Upvertise.models.sponsorship.SponsorshipStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record SponsorshipLightResponse(
        //dto having no providership data and with supplier transactiond data
        Integer id,
        SponsorshipStatus status,
        String userId,
        SponsorOfferResponse sponsorOffer,
        Set<SponsorAdResponse> sponsorAds,
        LocalDateTime createdDate,
        List<SupplierTransactionLightResponse> supplierTransactions
) {
}

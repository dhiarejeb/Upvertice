package com.dhia.Upvertise.dto;

import com.dhia.Upvertise.models.sponsorship.SponsorAd;
import com.dhia.Upvertise.models.supplier.SupplierOfferStatus;


import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
@Builder
public record SupplierOfferResponse(
        Integer id,
        String title,
        String description,
        Integer quantityAvailable,
        Double price,
        String startDate,  // Changed to String
        String endDate,    // Changed to String
        SupplierOfferStatus status,
        String imageUrl,
        List<SponsorAdResponse> sponsorAds
) {
}

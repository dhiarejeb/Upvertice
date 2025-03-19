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
        LocalDate startDate,
        LocalDate endDate,
        SupplierOfferStatus status,
        String imageUrl,
        List<SponsorAd> sponsorAds
) {
}

package com.dhia.Upvertise.dto;

import com.dhia.Upvertise.models.supplier.SupplierOfferStatus;

import java.time.LocalDate;
import java.util.List;

public record SupplierOfferRequest(
        String title,
        String description,
        Integer quantityAvailable,
        Double price,
        LocalDate startDate,
        LocalDate endDate,
        SupplierOfferStatus status,
        //List<String> proofDocs,
        List<Integer> sponsorAdIds // Admin provides the IDs of associated SponsorAds

) {
}

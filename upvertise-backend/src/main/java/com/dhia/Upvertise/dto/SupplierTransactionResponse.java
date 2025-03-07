package com.dhia.Upvertise.dto;

import com.dhia.Upvertise.models.supplier.SupplierTransactionStatus;

import java.util.List;

public record SupplierTransactionResponse(
        Integer id,
        String userId,
        SupplierOfferResponse supplierOffer,
        SupplierTransactionStatus supplierTransactionStatus,
        Integer quantitySold,
        Double relativePrice,
        Double percentage,
        List<String> proofs,
        List<String> locations,
        SponsorshipLightsResponse sponsorship

) {}

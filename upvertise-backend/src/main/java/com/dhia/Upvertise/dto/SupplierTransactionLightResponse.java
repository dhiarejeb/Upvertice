package com.dhia.Upvertise.dto;

import com.dhia.Upvertise.models.supplier.SupplierTransactionStatus;

import java.util.List;

public record SupplierTransactionLightResponse(
        Integer id,
        String userId,
        SupplierOfferResponse supplierOffer,
        SupplierTransactionStatus supplierTransactionStatus,
        Integer quantitySold,
        Double relativePrice,
        Double percentage,
        List<String> proofs

) {
}

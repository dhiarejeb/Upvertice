package com.dhia.Upvertise.mapper;

import com.dhia.Upvertise.dto.*;
import com.dhia.Upvertise.models.supplier.SupplierTransaction;



public class SupplierTransactionMapper {


    public static SupplierTransactionResponse toSupplierTransactionResponse(SupplierTransaction transaction) {
        if (transaction == null) {
            return null;
        }

        // Map related sponsorship details (which already include its providerships)
        SponsorshipLightsResponse sponsorshipLightsResponse = SponsorshipMapper.toSponsorshipLightsResponse(transaction.getSponsorship());

        // Map related supplier offer details
        SupplierOfferResponse supplierOfferResponse = SupplierOfferMapper.toResponse(transaction.getSupplierOffer());

        return new SupplierTransactionResponse(
                transaction.getId(),
                transaction.getUserId(),
                supplierOfferResponse,
                transaction.getSupplierTransactionStatus(),
                transaction.getQuantitySold(),
                transaction.getRelativePrice(),
                transaction.getPercentage(),
                sponsorshipLightsResponse
        );
    }

    public static SupplierTransactionLightResponse toSupplierTransactionLightResponse(SupplierTransaction transaction) {

        if (transaction == null) {
            return null;
        }
        // Map related supplier offer details
        SupplierOfferResponse supplierOfferResponse = SupplierOfferMapper.toResponse(transaction.getSupplierOffer());

        return new SupplierTransactionLightResponse(
                transaction.getId(),
                transaction.getUserId(),
                supplierOfferResponse,
                transaction.getSupplierTransactionStatus(),
                transaction.getQuantitySold(),
                transaction.getRelativePrice(),
                transaction.getPercentage()

        );
    }
}

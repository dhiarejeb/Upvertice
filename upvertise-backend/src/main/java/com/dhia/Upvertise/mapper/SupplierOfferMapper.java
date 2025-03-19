package com.dhia.Upvertise.mapper;

import com.dhia.Upvertise.dto.SupplierOfferRequest;
import com.dhia.Upvertise.dto.SupplierOfferResponse;
import com.dhia.Upvertise.models.sponsorship.SponsorAd;
import com.dhia.Upvertise.models.supplier.SupplierOffer;

import java.util.List;
import java.util.stream.Collectors;

// SupplierOfferMapper

public class SupplierOfferMapper {
    public static SupplierOfferResponse toResponse(SupplierOffer supplierOffer) {
        return SupplierOfferResponse.builder()
                .title(supplierOffer.getTitle())
                .description(supplierOffer.getDescription())
                .id(supplierOffer.getId())
                .quantityAvailable(supplierOffer.getQuantityAvailable())
                .price(supplierOffer.getPrice())
                .startDate(supplierOffer.getStartDate())
                .endDate(supplierOffer.getEndDate())
                .status(supplierOffer.getStatus())
                .imageUrl(supplierOffer.getImageUrl())
                .sponsorAds(supplierOffer.getSponsorAds())  // Return the full SponsorAds list
                .build();
    }


    public static SupplierOffer toSupplierOffer(SupplierOfferRequest request, List<SponsorAd> sponsorAds) {
        return SupplierOffer.builder()
                .title(request.title())
                .description(request.description())
                .quantityAvailable(request.quantityAvailable())
                .price(request.price())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .status(request.status())
                .sponsorAds(sponsorAds) // Set fetched SponsorAds
                .build();
        // Image URL will be set after Cloudinary upload
    }
    public static void updateSupplierOffer(SupplierOffer entity, SupplierOfferRequest request) {
        entity.setTitle(request.title());
        entity.setDescription(request.description());
        entity.setQuantityAvailable(request.quantityAvailable());
        entity.setPrice(request.price());
        entity.setStartDate(request.startDate());
        entity.setEndDate(request.endDate());
        entity.setStatus(request.status());
    }

    public static SupplierOfferResponse toResponseWithImageUrl(SupplierOffer supplierOffer) {
        // Build the SupplierOfferResponse with the builder pattern
        SupplierOfferResponse response = SupplierOfferResponse.builder()
                .id(supplierOffer.getId())
                .title(supplierOffer.getTitle())
                .description(supplierOffer.getDescription())
                .quantityAvailable(supplierOffer.getQuantityAvailable())
                .price(supplierOffer.getPrice())
                .startDate(supplierOffer.getStartDate())
                .endDate(supplierOffer.getEndDate())
                .status(supplierOffer.getStatus())
                .sponsorAds(supplierOffer.getSponsorAds())  // Add SponsorAds if needed
                .imageUrl(supplierOffer.getImageUrl())     // Add image URL if exists
                .build();

        return response;
    }
    }



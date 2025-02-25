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
                .sponsorAdIds(supplierOffer.getSponsorAds().stream()
                        .map(SponsorAd::getId)
                        .collect(Collectors.toList()))
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
    }


